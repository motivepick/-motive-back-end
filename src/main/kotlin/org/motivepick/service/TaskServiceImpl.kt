package org.motivepick.service

import org.motivepick.domain.entity.TaskEntity
import org.motivepick.domain.entity.TaskListEntity
import org.motivepick.domain.entity.TaskListType
import org.motivepick.domain.entity.TaskListType.*
import org.motivepick.domain.entity.UserEntity
import org.motivepick.domain.model.ScheduledTask
import org.motivepick.domain.view.CreateTaskRequest
import org.motivepick.domain.view.RescheduleTaskRequest
import org.motivepick.domain.view.TaskView
import org.motivepick.domain.view.UpdateTaskRequest
import org.motivepick.exception.ClientErrorException
import org.motivepick.exception.ResourceNotFoundException
import org.motivepick.extensions.CurrentUserExtensions.owns
import org.motivepick.extensions.ListExtensions.withPageable
import org.motivepick.extensions.TaskEntityExtensions.view
import org.motivepick.repository.TaskListRepository
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.motivepick.security.CurrentUser
import org.motivepick.security.UserNotAuthorizedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import kotlin.jvm.optionals.getOrNull

@Service
internal class TaskServiceImpl(
    private val tasksFactory: InitialTasksFactory,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val currentUser: CurrentUser,
    private val taskListRepository: TaskListRepository,
    private val scheduleFactory: ScheduleFactory
) : TaskService {

    private val logger: Logger = LoggerFactory.getLogger(TaskServiceImpl::class.java)

    private val predefinedTaskListTypes = listOf(INBOX, CLOSED, SCHEDULE_SECTION, DELETED)

    @Transactional
    override fun findTaskById(taskId: Long): TaskView? {
        val task = taskRepository.findByIdAndVisibleTrue(taskId).getOrNull()
        if (task == null) {
            return null
        } else if (currentUser.owns(task)) {
            return task.view()
        }
        throw UserNotAuthorizedException("The user does not own the task")
    }

    @Transactional
    override fun updateTaskById(taskId: Long, request: UpdateTaskRequest): TaskView? {
        val task = taskRepository.findByIdAndVisibleTrue(taskId).getOrNull()
        if (task == null) {
            return null
        } else if (currentUser.owns(task)) {
            request.name?.let { task.name = it.trim() }
            request.description?.let { task.description = it.trim() }
            request.created?.let { task.created = it }
            request.dueDate?.let { task.dueDate = it }
            request.closingDate?.let { task.closingDate = it }
            request.closed?.let { task.closed = it }
            if (request.deleteDueDate) {
                task.dueDate = null
            }
            return taskRepository.save(task).view()
        }
        throw UserNotAuthorizedException("The user does not own the task")
    }

    @Transactional
    override fun softDeleteTaskById(taskId: Long): TaskView? {
        val task = taskRepository.findByIdAndVisibleTrue(taskId).getOrNull()
        if (task == null) {
            return null
        } else if (currentUser.owns(task)) {
            task.visible = false
            return taskRepository.save(task).view()
        }
        throw UserNotAuthorizedException("The user does not own the task")
    }

    @Transactional
    override fun findForCurrentUser(listId: String, offset: Long, limit: Int): Page<TaskView> {
        val pageable = OffsetBasedPageable(offset, limit)
        val accountId = currentUser.getAccountId()
        val taskList = findTaskList(accountId, listId) ?: throw ResourceNotFoundException("Task list with ID or type $listId not found for user $accountId")
        val taskIdsPage = taskList.orderedIds.withPageable(pageable)
        val tasks = taskRepository.findAllByIdInAndVisibleTrue(taskIdsPage.content).map { it.view() }
        val taskToId: Map<Long?, TaskView> = tasks.associateBy { it.id }
        return PageImpl(taskIdsPage.mapNotNull { taskToId[it] }, pageable, taskIdsPage.totalElements)
    }

    @Transactional
    override fun findScheduleForCurrentUser(timeZone: ZoneId): List<ScheduledTask> {
        val schedule = findOrCreateSchedule()
        val taskToId = taskRepository
            .findAllByIdInAndVisibleTrue(schedule.orderedIds)
            .map { ScheduledTask.from(it) }
            .associateBy { it.id }
        return schedule
            .orderedIds
            .mapNotNull { taskToId[it] }
    }

    private fun findOrCreateSchedule(): TaskListEntity {
        val user = userRepository.findByAccountId(currentUser.getAccountId())!!
        val existingSchedule = taskListRepository.findByUserAccountIdAndType(user.accountId, SCHEDULE)
        if (existingSchedule == null) {
            val ids = taskRepository.findAllByUserAccountIdAndClosedFalseAndDueDateNotNullAndVisibleTrueOrderByDueDateAsc(currentUser.getAccountId())
                .map { it.id }
            return taskListRepository.save(TaskListEntity(user, SCHEDULE, ids))
        }
        return existingSchedule
    }

    @Transactional
    override fun createTask(request: CreateTaskRequest): TaskView {
        val user = userRepository.findByAccountId(currentUser.getAccountId())!!
        val task = taskRepository.save(taskFromRequest(user, request))
        val taskList = taskListRepository.findByUserAccountIdAndType(user.accountId, INBOX)!!
        taskList.addTask(task)
        taskListRepository.save(taskList)
        logger.info("Created a task with ID {}", task.id)
        return task.view()
    }

    @Transactional
    override fun createInitialTasks(tasksOwner: UserEntity, language: String) {
        val tasks = tasksFactory.createInitialTasks(tasksOwner, language)
        val inbox = taskListRepository.save(TaskListEntity(tasksOwner, INBOX, listOf()))
        val closed = taskListRepository.save(TaskListEntity(tasksOwner, CLOSED, listOf()))
        tasks[INBOX]!!.forEach { it.taskList = inbox }
        tasks[CLOSED]!!.forEach { it.taskList = closed }
        val savedInboxTasks = taskRepository.saveAll(tasks[INBOX]!!)
        val savedClosedTasks = taskRepository.saveAll(tasks[CLOSED]!!)
        inbox.orderedIds = savedInboxTasks.mapNotNull { it.id }
        closed.orderedIds = savedClosedTasks.mapNotNull { it.id }
        taskListRepository.saveAll(listOf(inbox, closed))
    }

    @Transactional
    override fun migrateTasks(fromUserAccountId: String, toUserAccountId: String) {
        val toUser = userRepository.findByAccountId(toUserAccountId)!!

        val tasks = taskRepository.findAllByUserAccountId(fromUserAccountId)
        tasks.forEach { it.user = toUser }
        taskRepository.saveAll(tasks)

        val fromTaskLists = taskListRepository.findAllByUserAccountId(fromUserAccountId)
        val fromTaskListToType = fromTaskLists.associateBy { it.type }
        val toTaskLists = taskListRepository.findAllByUserAccountId(toUserAccountId)
        val toTaskListToType = toTaskLists.associateBy { it.type }

        listOf(INBOX, CLOSED).forEach { type ->
            val fromTaskList = fromTaskListToType[type]!!
            val toTaskList = toTaskListToType[type]!!
            fromTaskList.tasks.forEach(toTaskList::addTask)
            fromTaskList.tasks.removeAll { true }
        }
        taskListRepository.deleteByIdIn(fromTaskLists.map { it.id })
        taskListRepository.saveAll(toTaskLists)
        userRepository.deleteByAccountId(fromUserAccountId)
    }

    @Transactional
    override fun deleteTasksFully(userAccountId: String) {
        taskListRepository.deleteByUserAccountId(userAccountId)
        taskRepository.deleteByUserAccountId(userAccountId)
    }

    @Transactional
    override fun rescheduleTask(taskId: Long, request: RescheduleTaskRequest): ScheduledTask {
        val task = taskRepository.findByIdAndVisibleTrue(taskId).getOrNull() ?: throw ResourceNotFoundException("Task with ID $taskId not found")
        task.dueDate = request.dueDate
        val userId = currentUser.getAccountId()
        val taskList = taskListRepository.findByUserAccountIdAndType(userId, SCHEDULE)
            ?: throw ResourceNotFoundException("Task list with type $SCHEDULE not found for user $userId")
        taskList.orderedIds = request.taskIds
        taskListRepository.save(taskList)
        return ScheduledTask.from(taskRepository.save(task))
    }

    private fun taskFromRequest(user: UserEntity, request: CreateTaskRequest): TaskEntity {
        val task = TaskEntity(user, request.name.trim())
        task.description = request.description?.trim()
        task.dueDate = request.dueDate
        return task
    }

    private fun findTaskList(accountId: String, listId: String): TaskListEntity? {
        try {
            val listType = TaskListType.valueOf(listId)
            if (predefinedTaskListTypes.contains(listType)) {
                return taskListRepository.findByUserAccountIdAndType(accountId, listType)
            }
            throw ClientErrorException("Invalid task list ID: $listId, must be one of $predefinedTaskListTypes")
        } catch (e: IllegalArgumentException) {
            try {
                return taskListRepository.findByUserAccountIdAndId(accountId, listId.toLong())
            } catch (e: NumberFormatException) {
                throw ClientErrorException("Invalid task list ID: $listId, must be a number or one of ${TaskListType.entries.toTypedArray()}")
            }
        }
    }
}

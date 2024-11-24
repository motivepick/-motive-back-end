package org.motivepick.service

import org.motivepick.domain.entity.TaskEntity
import org.motivepick.domain.entity.TaskListEntity
import org.motivepick.domain.entity.TaskListType
import org.motivepick.domain.entity.TaskListType.*
import org.motivepick.domain.entity.UserEntity
import org.motivepick.domain.model.ScheduledTask
import org.motivepick.domain.view.*
import org.motivepick.exception.ClientErrorException
import org.motivepick.exception.ResourceNotFoundException
import org.motivepick.extensions.CurrentUserExtensions.owns
import org.motivepick.extensions.ListExtensions.withPageable
import org.motivepick.extensions.ScheduledTaskExtensions.view
import org.motivepick.extensions.TaskEntityExtensions.view
import org.motivepick.repository.TaskListRepository
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.motivepick.security.CurrentUser
import org.motivepick.security.UserNotAuthorizedException
import org.motivepick.service.Constants.EXCLUSIVE_TASK_LISTS
import org.motivepick.service.Constants.INCLUSIVE_TASK_LISTS
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
internal class TaskServiceImpl(
    private val tasksFactory: InitialTasksFactory,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val currentUser: CurrentUser,
    private val taskListRepository: TaskListRepository
) : TaskService {

    private val logger: Logger = LoggerFactory.getLogger(TaskServiceImpl::class.java)

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
            request.closingDate?.let { task.closingDate = it }
            request.closed?.let { task.closed = it }
            if (request.deleteDueDate) {
                task.dueDate = null
                val schedule = taskListRepository.findByUserAccountIdAndType(currentUser.getAccountId(), SCHEDULE)!!
                schedule.orderedIds = schedule.orderedIds.filter { it != task.id }
                taskListRepository.save(schedule)
            } else if (request.dueDate != null) {
                if (task.dueDate == null) {
                    val schedule = taskListRepository.findByUserAccountIdAndType(currentUser.getAccountId(), SCHEDULE)!!
                    schedule.orderedIds = listOf(task.id) + schedule.orderedIds
                    taskListRepository.save(schedule)
                }
                task.dueDate = request.dueDate
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
    override fun findScheduleForCurrentUser(): List<ScheduledTaskView> {
        val schedule = findOrCreateSchedule()
        val openTaskIds = HashSet(taskListRepository.findByUserAccountIdAndType(currentUser.getAccountId(), INBOX)?.orderedIds ?: emptyList())
        val scheduleTaskIds = schedule.orderedIds.filter { openTaskIds.contains(it) }
        val taskToId = taskRepository
            .findAllByIdInAndVisibleTrue(scheduleTaskIds)
            .map { ScheduledTask.from(it) }
            .associateBy { it.id }
        return scheduleTaskIds
            .mapNotNull { taskToId[it] }
            .map { it.view() }
    }

    @Transactional
    override fun createTask(request: CreateTaskRequest): TaskView {
        val user = userRepository.findByAccountId(currentUser.getAccountId())!!
        val task = taskRepository.save(taskFromRequest(user, request))
        val taskList = taskListRepository.findByUserAccountIdAndType(user.accountId, INBOX)!!
        taskList.prependTask(task)
        taskListRepository.save(taskList)
        if (task.dueDate != null) {
            val schedule = findOrCreateSchedule()
            schedule.orderedIds = listOf(task.id) + schedule.orderedIds
            taskListRepository.save(schedule)
        }
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
        createSchedule(tasksOwner)
    }

    @Transactional
    override fun migrateTasks(srcUserAccountId: String, dstUserAccountId: String) {
        val dstUser = userRepository.findByAccountId(dstUserAccountId)!!

        val tasks = taskRepository.findAllByUserAccountId(srcUserAccountId)
        tasks.forEach { it.user = dstUser }
        taskRepository.saveAll(tasks)

        val srcTaskLists = taskListRepository.findAllByUserAccountId(srcUserAccountId)

        srcTaskLists
            .filter { EXCLUSIVE_TASK_LISTS.contains(it.type) }
            .forEach { srcTaskList ->
                val dstTaskList = findOrCreateTaskList(dstUser, srcTaskList.type)
                srcTaskList.tasks.forEach(dstTaskList::prependTask)
                srcTaskList.tasks.removeAll { true }
                taskListRepository.save(dstTaskList)
            }

        srcTaskLists
            .filter { INCLUSIVE_TASK_LISTS.contains(it.type) }
            .forEach { srcTaskList ->
                val dstTaskList = findOrCreateTaskList(dstUser, srcTaskList.type)
                val existingTaskIds = dstTaskList.orderedIds.toHashSet()
                dstTaskList.orderedIds = srcTaskList.orderedIds.filterNot(existingTaskIds::contains) + dstTaskList.orderedIds
                taskListRepository.save(dstTaskList)
            }

        taskListRepository.deleteByIdIn(srcTaskLists.map { it.id })
        userRepository.deleteByAccountId(srcUserAccountId)
    }

    @Transactional
    override fun deleteTasksFully(userAccountId: String) {
        taskListRepository.deleteByUserAccountId(userAccountId)
        taskRepository.deleteByUserAccountId(userAccountId)
    }

    @Transactional
    override fun rescheduleTask(taskId: Long, request: RescheduleTaskRequest): ScheduledTaskView {
        val task = taskRepository.findByIdAndVisibleTrue(taskId).getOrNull() ?: throw ResourceNotFoundException("Task with ID $taskId not found")
        task.dueDate = request.dueDate
        val userId = currentUser.getAccountId()
        val taskList = taskListRepository.findByUserAccountIdAndType(userId, SCHEDULE)
            ?: throw ResourceNotFoundException("Task list with type $SCHEDULE not found for user $userId")
        taskList.orderedIds = request.taskIds
        taskListRepository.save(taskList)
        return ScheduledTask.from(taskRepository.save(task)).view()
    }

    private fun findOrCreateTaskList(owner: UserEntity, taskListType: TaskListType) =
        taskListRepository.findByUserAccountIdAndType(owner.accountId, taskListType) ?: taskListRepository.save(TaskListEntity(owner, taskListType, emptyList()))

    private fun taskFromRequest(user: UserEntity, request: CreateTaskRequest): TaskEntity {
        val task = TaskEntity(user, request.name.trim())
        task.description = request.description?.trim()
        task.dueDate = request.dueDate
        return task
    }

    private fun findTaskList(accountId: String, listId: String): TaskListEntity? {
        try {
            val listType = TaskListType.valueOf(listId)
            if (listType == CUSTOM) {
                throw ClientErrorException("Use specific list ID to fetch tasks from a list of type $listType")
            }
            return taskListRepository.findByUserAccountIdAndType(accountId, listType)
        } catch (e: IllegalArgumentException) {
            try {
                return taskListRepository.findByUserAccountIdAndId(accountId, listId.toLong())
            } catch (e: NumberFormatException) {
                throw ClientErrorException("Invalid task list ID: $listId, must be a number or one of ${TaskListType.entries.toTypedArray()}")
            }
        }
    }

    private fun findOrCreateSchedule(): TaskListEntity {
        val user = userRepository.findByAccountId(currentUser.getAccountId())!!
        return taskListRepository.findByUserAccountIdAndType(user.accountId, SCHEDULE) ?: createSchedule(user)
    }

    private fun createSchedule(tasksOwner: UserEntity): TaskListEntity {
        val ids = taskRepository.findAllByUserAccountIdAndClosedFalseAndDueDateNotNullAndVisibleTrueOrderByDueDateAsc(tasksOwner.accountId)
            .map { it.id }
        return taskListRepository.save(TaskListEntity(tasksOwner, SCHEDULE, ids))
    }
}

package org.motivepick.service

import org.motivepick.domain.entity.TaskEntity
import org.motivepick.domain.entity.TaskListEntity
import org.motivepick.domain.entity.TaskListType
import org.motivepick.domain.entity.TaskListType.CLOSED
import org.motivepick.domain.entity.TaskListType.INBOX
import org.motivepick.domain.entity.UserEntity
import org.motivepick.domain.ui.task.CreateTaskRequest
import org.motivepick.domain.ui.task.UpdateTaskRequest
import org.motivepick.repository.TaskListRepository
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.motivepick.security.CurrentUser
import org.motivepick.security.UserNotAuthorizedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
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
    override fun findTaskById(taskId: Long): TaskEntity? {
        val task = taskRepository.findByIdAndVisibleTrue(taskId).getOrNull()
        if (task == null) {
            return null
        } else if (currentUserOwns(task)) {
            return task
        }
        throw UserNotAuthorizedException("The user does not own the task")
    }

    @Transactional
    override fun updateTaskById(taskId: Long, request: UpdateTaskRequest): TaskEntity? {
        val task = taskRepository.findByIdAndVisibleTrue(taskId).getOrNull()
        if (task == null) {
            return null
        } else if (currentUserOwns(task)) {
            request.name?.let { task.name = it.trim() }
            request.description?.let { task.description = it.trim() }
            request.created?.let { task.created = it }
            request.dueDate?.let { task.dueDate = it }
            request.closingDate?.let { task.closingDate = it }
            request.closed?.let { task.closed = it }
            if (request.deleteDueDate) {
                task.dueDate = null
            }
            return taskRepository.save(task)
        }
        throw UserNotAuthorizedException("The user does not own the task")
    }

    @Transactional
    override fun softDeleteTaskById(taskId: Long): TaskEntity? {
        val task = taskRepository.findByIdAndVisibleTrue(taskId).getOrNull()
        if (task == null) {
            return null
        } else if (currentUserOwns(task)) {
            task.visible = false
            return taskRepository.save(task)
        }
        throw UserNotAuthorizedException("The user does not own the task")
    }

    @Transactional
    override fun findForCurrentUser(listType: TaskListType, offset: Int, limit: Int): Page<TaskEntity> {
        val pageable: Pageable = OffsetBasedPageRequest(offset, limit)
        val accountId = currentUser.getAccountId()
        val taskList = taskListRepository.findByUserAccountIdAndType(accountId, listType)
        val taskIdsPage = Lists.withPageable(taskList!!.orderedIds, pageable)
        val tasks = taskRepository.findAllByIdInAndVisibleTrue(taskIdsPage.content)
        val taskToId: Map<Long?, TaskEntity> = tasks.associateBy { it.id }
        return PageImpl(taskIdsPage.mapNotNull { taskToId[it] }, pageable, taskIdsPage.totalElements)
    }

    @Transactional
    override fun createTask(request: CreateTaskRequest): TaskEntity {
        val user = userRepository.findByAccountId(currentUser.getAccountId())!!
        val task = taskRepository.save(taskFromRequest(user, request))
        val taskList = taskListRepository.findByUserAccountIdAndType(user.accountId, INBOX)!!
        taskList.addTask(task)
        taskListRepository.save(taskList)
        logger.info("Created a task with ID {}", task.id)
        return task
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

    private fun taskFromRequest(user: UserEntity, request: CreateTaskRequest): TaskEntity {
        val task = TaskEntity(user, request.name.trim())
        task.description = request.description?.trim()
        task.dueDate = request.dueDate
        return task
    }

    private fun currentUserOwns(task: TaskEntity) = task.user.accountId == currentUser.getAccountId()
}

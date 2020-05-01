package org.motivepick.service

import org.motivepick.domain.entity.Task
import org.motivepick.domain.entity.TaskListEntity
import org.motivepick.domain.entity.TaskListType
import org.motivepick.domain.entity.TaskListType.CLOSED
import org.motivepick.domain.entity.TaskListType.INBOX
import org.motivepick.domain.entity.User
import org.motivepick.domain.ui.task.CreateTaskRequest
import org.motivepick.repository.TaskListRepository
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.motivepick.security.CurrentUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskServiceImpl(private val tasksFactory: InitialTasksFactory, private val userRepository: UserRepository,
        private val taskRepository: TaskRepository, private val currentUser: CurrentUser,
        private val taskListRepository: TaskListRepository) : TaskService {

    @Transactional
    override fun findForCurrentUser(): List<Task> {
        val accountId = currentUser.getAccountId()
        return taskRepository.findAllByUserAccountIdAndVisibleTrueOrderByCreatedDesc(accountId)
    }

    @Transactional
    override fun findForCurrentUser(listType: TaskListType, offset: Int, limit: Int): Page<Task> {
        val pageable: Pageable = OffsetBasedPageRequest(offset, limit)
        val accountId = currentUser.getAccountId()
        val taskList = taskListRepository.findByUserAccountIdAndType(accountId, listType)
        val taskIdsPage = Lists.withPageable(taskList!!.orderedIds.filterNotNull(), pageable)
        val tasks = taskRepository.findAllByIdInAndVisibleTrue(taskIdsPage.content)
        val taskToId: Map<Long?, Task> = tasks.map { it.id to it }.toMap()
        return PageImpl(taskIdsPage.mapNotNull { taskToId[it] }, pageable, taskIdsPage.totalElements)
    }

    @Transactional
    override fun createTask(request: CreateTaskRequest): Task {
        val user = userRepository.findByAccountId(currentUser.getAccountId())!!
        val task = taskRepository.save(taskFromRequest(user, request))
        val taskList = taskListRepository.findByUserAccountIdAndType(user.accountId, INBOX)!!
        taskList.addTask(task)
        taskListRepository.save(taskList)
        return task
    }

    @Transactional
    override fun createInitialTasks(tasksOwner: User, language: String) {
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
        val fromTaskListToType = fromTaskLists.map { it.type to it }.toMap()
        val toTaskLists = taskListRepository.findAllByUserAccountId(toUserAccountId)
        val toTaskListToType = toTaskLists.map { it.type to it }.toMap()

        listOf(INBOX, CLOSED).forEach { type ->
            val fromTaskList = fromTaskListToType[type]!!
            val toTaskList = toTaskListToType[type]!!
            fromTaskList.tasks.forEach(toTaskList::addTask)
            fromTaskList.tasks.removeAll { true }
        }
        taskListRepository.deleteByIdIn(fromTaskLists.mapNotNull { it.id })
        taskListRepository.saveAll(toTaskLists)
        userRepository.deleteByAccountId(fromUserAccountId)
    }

    @Transactional
    override fun deleteTasksFully(userAccountId: String) {
        taskListRepository.deleteByUserAccountId(userAccountId)
        taskRepository.deleteByUserAccountId(userAccountId)
    }

    private fun taskFromRequest(user: User, request: CreateTaskRequest): Task {
        val task = Task(user, request.name.trim())
        task.description = request.description?.trim()
        task.dueDate = request.dueDate
        return task
    }
}

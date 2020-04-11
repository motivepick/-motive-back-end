package org.motivepick.service

import org.motivepick.domain.entity.*
import org.motivepick.domain.entity.TaskListType.CLOSED_TASKS
import org.motivepick.domain.entity.TaskListType.INBOX
import org.motivepick.domain.ui.task.CreateTaskRequest
import org.motivepick.repository.TaskListRepository
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.TasksOrderRepository
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
        private val tasksOrderRepository: TasksOrderRepository, private val tasksOrderService: TasksOrderService,
        private val taskListRepository: TaskListRepository) : TaskService {

    @Transactional
    override fun findForCurrentUser(): List<Task> {
        val accountId = currentUser.getAccountId()
        return taskRepository.findAllByUserAccountIdAndVisibleTrueOrderByCreatedDesc(accountId)
    }

    @Transactional
    override fun findForCurrentUser(listType: TaskListType, pageable: Pageable): Page<Task> {
        val accountId = currentUser.getAccountId()
        val taskIdsPage = tasksOrderService.findOrder(accountId, listType, pageable)
        val tasks = taskRepository.findAllByIdIn(taskIdsPage.content)
        val taskToId: Map<Long?, Task> = tasks.map { it.id to it }.toMap()
        return PageImpl(taskIdsPage.mapNotNull { taskToId[it] }, pageable, taskIdsPage.totalElements)
    }

    @Transactional
    override fun createTask(request: CreateTaskRequest): Task {
        val user = userRepository.findByAccountId(currentUser.getAccountId())!!
        val task = Task(user, request.name.trim())
        task.description = request.description?.trim()
        task.dueDate = request.dueDate
        val createdTask = taskRepository.save(task)
        tasksOrderService.addTask(currentUser.getAccountId(), createdTask.id!!)
        return createdTask
    }

    @Transactional
    override fun createInitialTasks(tasksOwner: User): Iterable<Task> {
        val tasks = tasksFactory.createInitialTasks(tasksOwner)
        val taskLists = taskListRepository.saveAll(listOf(TaskListEntity(tasksOwner, INBOX), TaskListEntity(tasksOwner, CLOSED_TASKS)))
        val inbox = taskLists.first { it.type == INBOX }
        val closedTasks = taskLists.first { it.type == CLOSED_TASKS }
        tasks.forEach { it.taskList = inbox }
        val savedTasks = taskRepository.saveAll(tasks)
        tasksOrderRepository.saveAll(listOf(TasksOrderEntity(tasksOwner, savedTasks.map { it.id }, inbox),
                TasksOrderEntity(tasksOwner, listOf(), closedTasks)))
        return savedTasks
    }

    @Transactional
    override fun migrateTasks(fromUserAccountId: String, toUserAccountId: String) {
        val toUser = userRepository.findByAccountId(toUserAccountId)!!

        val tasks = taskRepository.findAllByUserAccountId(fromUserAccountId)
        tasks.forEach { it.user = toUser }
        taskRepository.saveAll(tasks)

        val orders = tasksOrderRepository.findAllByUserAccountId(fromUserAccountId)
        orders.forEach { it.user = toUser }
        tasksOrderRepository.saveAll(orders)

        // TODO: migrate task lists
    }

    @Transactional
    override fun deleteTasksFully(userAccountId: String) {
        tasksOrderService.deleteTasksOrders(userAccountId)
        taskListRepository.deleteByUserAccountId(userAccountId)
        taskRepository.deleteByUserAccountId(userAccountId)
    }
}

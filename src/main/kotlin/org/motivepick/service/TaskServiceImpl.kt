package org.motivepick.service

import org.motivepick.domain.entity.Task
import org.motivepick.domain.entity.User
import org.motivepick.domain.ui.task.CreateTaskRequest
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.motivepick.security.CurrentUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskServiceImpl(private val tasksFactory: InitialTasksFactory, private val userRepository: UserRepository,
        private val taskRepository: TaskRepository, private val currentUser: CurrentUser, private val tasksOrderService: TasksOrderService) : TaskService {

    @Transactional
    override fun findAllForCurrentUser(): List<Task> {
        val accountId = currentUser.getAccountId()
        val tasks = taskRepository.findAllByUserAccountIdAndVisibleTrueOrderByCreatedDesc(accountId)
        return tasksOrderService.ordered(accountId, tasks)
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
        return taskRepository.saveAll(tasks)
    }

    @Transactional
    override fun migrateTasks(fromUserAccountId: String, toUserAccountId: String) {
        val tasks = taskRepository.findAllByUserAccountId(fromUserAccountId)
        val toUser = userRepository.findByAccountId(toUserAccountId)!!
        tasks.forEach { it.user = toUser }
        taskRepository.saveAll(tasks)
    }
}

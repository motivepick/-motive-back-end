package org.motivepick.service

import org.motivepick.domain.entity.Task
import org.motivepick.domain.entity.User
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskServiceImpl(private val tasksFactory: InitialTasksFactory, private val userRepository: UserRepository,
        private val taskRepository: TaskRepository) : TaskService {

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

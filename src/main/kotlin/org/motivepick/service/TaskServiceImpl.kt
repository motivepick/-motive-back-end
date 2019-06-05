package org.motivepick.service

import org.motivepick.domain.entity.Task
import org.motivepick.domain.entity.User
import org.motivepick.repository.TaskRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskServiceImpl(private val tasksFactory: InitialTasksFactory, private val taskRepository: TaskRepository) : TaskService {

    @Transactional
    override fun createInitialTasks(tasksOwner: User): Iterable<Task> {
        val tasks = tasksFactory.createInitialTasks(tasksOwner)
        return taskRepository.saveAll(tasks)
    }
}

package org.motivepick.service

import org.motivepick.domain.entity.Task
import org.motivepick.domain.entity.User
import org.motivepick.domain.ui.task.CreateTaskRequest

interface TaskService {

    fun findAllForCurrentUser(): List<Task>

    fun createTask(request: CreateTaskRequest): Task

    fun createInitialTasks(tasksOwner: User): Iterable<Task>

    fun migrateTasks(fromUserAccountId: String, toUserAccountId: String)
}

package org.motivepick.service

import org.motivepick.domain.entity.Task
import org.motivepick.domain.entity.TaskListType
import org.motivepick.domain.entity.User
import org.motivepick.domain.ui.task.CreateTaskRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TaskService {

    fun findForCurrentUser(): List<Task>

    fun findForCurrentUser(listType: TaskListType, pageable: Pageable): Page<Task>

    fun createTask(request: CreateTaskRequest): Task

    fun createInitialTasks(tasksOwner: User): Iterable<Task>

    fun migrateTasks(fromUserAccountId: String, toUserAccountId: String)

    fun deleteTasksFully(userAccountId: String)
}

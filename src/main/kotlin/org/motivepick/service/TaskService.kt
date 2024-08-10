package org.motivepick.service

import org.motivepick.domain.entity.TaskEntity
import org.motivepick.domain.entity.TaskListType
import org.motivepick.domain.entity.UserEntity
import org.motivepick.domain.ui.task.CreateTaskRequest
import org.motivepick.domain.ui.task.UpdateTaskRequest
import org.springframework.data.domain.Page

interface TaskService {

    fun findTaskById(taskId: Long): TaskEntity?

    fun updateTaskById(taskId: Long, request: UpdateTaskRequest): TaskEntity?

    fun softDeleteTaskById(taskId: Long): TaskEntity?

    fun findForCurrentUser(listType: TaskListType, offset: Int, limit: Int): Page<TaskEntity>

    fun createTask(request: CreateTaskRequest): TaskEntity

    fun createInitialTasks(tasksOwner: UserEntity, language: String)

    fun migrateTasks(fromUserAccountId: String, toUserAccountId: String)

    fun deleteTasksFully(userAccountId: String)
}

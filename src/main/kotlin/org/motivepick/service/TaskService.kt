package org.motivepick.service

import org.motivepick.domain.entity.UserEntity
import org.motivepick.domain.model.ScheduledTask
import org.motivepick.domain.view.CreateTaskRequest
import org.motivepick.domain.view.ScheduleView
import org.motivepick.domain.view.TaskView
import org.motivepick.domain.view.UpdateTaskRequest
import org.springframework.data.domain.Page
import java.time.ZoneId

interface TaskService {

    fun findTaskById(taskId: Long): TaskView?

    fun updateTaskById(taskId: Long, request: UpdateTaskRequest): TaskView?

    fun softDeleteTaskById(taskId: Long): TaskView?

    fun findForCurrentUser(listId: String, offset: Long, limit: Int): Page<TaskView>

    fun findScheduleForCurrentUser(timeZone: ZoneId): List<ScheduledTask>

    fun createTask(request: CreateTaskRequest): TaskView

    fun createInitialTasks(tasksOwner: UserEntity, language: String)

    fun migrateTasks(fromUserAccountId: String, toUserAccountId: String)

    fun deleteTasksFully(userAccountId: String)
}

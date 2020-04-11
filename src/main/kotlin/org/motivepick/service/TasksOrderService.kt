package org.motivepick.service

import org.motivepick.domain.entity.TaskListType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TasksOrderService {

    fun findOrder(accountId: String, listType: TaskListType, pageable: Pageable): Page<Long>

    fun moveTask(accountId: String, sourceId: Long, destinationId: Long)

    fun addTask(accountId: String, taskId: Long)

    fun deleteTasksOrders(accountId: String)
}

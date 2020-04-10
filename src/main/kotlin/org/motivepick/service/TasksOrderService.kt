package org.motivepick.service

import org.motivepick.domain.entity.Task

interface TasksOrderService {

    fun ordered(accountId: String, tasks: List<Task>): List<Task>

    fun moveTask(accountId: String, sourceId: Long, destinationId: Long)

    fun addTask(accountId: String, taskId: Long)

    fun deleteTasksOrders(accountId: String)
}

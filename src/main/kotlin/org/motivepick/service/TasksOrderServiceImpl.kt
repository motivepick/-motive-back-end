package org.motivepick.service

import org.motivepick.domain.entity.Task
import org.springframework.stereotype.Service

val ACCOUNT_ID_TO_ORDER = mutableMapOf<String, MutableList<Long?>>()

@Service
class TasksOrderServiceImpl : TasksOrderService {

    override fun ordered(accountId: String, tasks: List<Task>): List<Task> {
        val order = findTasksOrderForUser(accountId)
        return if (order.isEmpty()) {
            val ids = tasks.map { it.id }
            saveTasksOrderForUser(accountId, ids)
            tasks
        } else {
            val taskToId: Map<Long?, Task> = tasks.map { it.id to it }.toMap()  // TODO: add the remaining tasks to the order and write warning
            order.map { taskToId[it]!! }
        }
    }

    // TODO: if you update a page very quickly after move, it will read tasks faster than the order was saved (probably because of the map, not database)
    override fun moveTask(accountId: String, sourceId: Long, destinationId: Long) {
        val order = ACCOUNT_ID_TO_ORDER[accountId]!!
        val destinationIndex = order.indexOf(destinationId)
        order.remove(sourceId)
        ACCOUNT_ID_TO_ORDER[accountId] = insertWithShift(order, destinationIndex, sourceId)
    }

    override fun addTask(accountId: String, taskId: Long) {
        val order = ACCOUNT_ID_TO_ORDER[accountId]!!
        ACCOUNT_ID_TO_ORDER[accountId] = insertWithShift(order, 0, taskId)
    }

    // TODO: refactor this method
    private fun insertWithShift(list: MutableList<Long?>, index: Int, element: Long?): MutableList<Long?> {
        val result: MutableList<Long?> = mutableListOf()
        for ((i, value) in list.iterator().withIndex()) {
            if (i == index) {
                result.add(element)
            }
            result.add(value)
        }
        if (index == list.size) {
            result.add(element)
        }
        return result
    }

    private fun findTasksOrderForUser(accountId: String): MutableList<Long?> {
        if (!ACCOUNT_ID_TO_ORDER.containsKey(accountId)) {
            ACCOUNT_ID_TO_ORDER[accountId] = ArrayList()
        }
        return ACCOUNT_ID_TO_ORDER[accountId]!!
    }

    private fun saveTasksOrderForUser(accountId: String, order: List<Long?>) {
        ACCOUNT_ID_TO_ORDER[accountId] = order.toMutableList()
    }
}

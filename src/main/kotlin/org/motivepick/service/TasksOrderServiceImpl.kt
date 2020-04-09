package org.motivepick.service

import org.motivepick.domain.entity.Task
import org.motivepick.domain.entity.TasksOrderEntity
import org.motivepick.repository.TasksOrderRepository
import org.motivepick.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class TasksOrderServiceImpl(private val userRepository: UserRepository, private val tasksOrderRepository: TasksOrderRepository) : TasksOrderService {

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
        val orderEntity = tasksOrderRepository.findByUserAccountId(accountId)!!
        val order = orderEntity.orderedIds.toMutableList()
        val destinationIndex = order.indexOf(destinationId)
        order.remove(sourceId)
        orderEntity.orderedIds = insertWithShift(order, destinationIndex, sourceId)
        tasksOrderRepository.save(orderEntity)
    }

    override fun addTask(accountId: String, taskId: Long) {
        val orderEntity = tasksOrderRepository.findByUserAccountId(accountId)!!
        val order = orderEntity.orderedIds.toMutableList()
        orderEntity.orderedIds = insertWithShift(order, 0, taskId)
        tasksOrderRepository.save(orderEntity)
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
        val orderEntity = tasksOrderRepository.findByUserAccountId(accountId)
        return orderEntity?.orderedIds?.toMutableList() ?: ArrayList()
    }

    private fun saveTasksOrderForUser(accountId: String, order: List<Long?>) {
        val user = userRepository.findByAccountId(accountId)
        tasksOrderRepository.save(TasksOrderEntity(user!!, order))
    }
}

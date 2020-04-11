package org.motivepick.service

import org.motivepick.domain.entity.TaskListType
import org.motivepick.repository.TaskListRepository
import org.motivepick.repository.TasksOrderRepository
import org.motivepick.repository.UserRepository
import org.motivepick.service.Lists.insertWithShift
import org.motivepick.service.Lists.withPageable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TasksOrderServiceImpl(private val userRepository: UserRepository, private val tasksOrderRepository: TasksOrderRepository,
        private val taskListRepository: TaskListRepository) : TasksOrderService {

    @Transactional
    override fun findOrder(accountId: String, listType: TaskListType, pageable: Pageable): Page<Long> {
        val taskList = taskListRepository.findByUserAccountIdAndType(accountId, listType)
        val orderEntity = tasksOrderRepository.findByTaskListId(taskList!!.id!!)
        return withPageable(orderEntity!!.orderedIds.filterNotNull(), pageable)
    }

    // TODO: if you update a page very quickly after move, it will read tasks faster than the order was saved (probably because of the map, not database)
    @Transactional
    override fun moveTask(accountId: String, sourceId: Long, destinationId: Long) {
        val orderEntity = tasksOrderRepository.findByUserAccountId(accountId)!!
        val order = orderEntity.orderedIds.toMutableList()
        val destinationIndex = order.indexOf(destinationId)
        order.remove(sourceId)
        orderEntity.orderedIds = insertWithShift(order, destinationIndex, sourceId)
        tasksOrderRepository.save(orderEntity)
    }

    @Transactional
    override fun addTask(accountId: String, taskId: Long) {
        val orderEntity = tasksOrderRepository.findByUserAccountId(accountId)!!
        val order = orderEntity.orderedIds.toMutableList()
        orderEntity.orderedIds = insertWithShift(order, 0, taskId)
        tasksOrderRepository.save(orderEntity)
    }

    @Transactional
    override fun deleteTasksOrders(accountId: String) {
        tasksOrderRepository.deleteByUserAccountId(accountId)
    }
}

package org.motivepick.service

import org.motivepick.domain.entity.TaskEntity
import org.motivepick.domain.entity.TaskListType
import org.motivepick.domain.entity.TaskListType.CLOSED
import org.motivepick.domain.entity.TaskListType.INBOX
import org.motivepick.repository.TaskListRepository
import org.motivepick.repository.TaskRepository
import org.motivepick.security.CurrentUser
import org.motivepick.service.Lists.insertBefore
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import java.util.Optional.empty

@Service
internal class TaskListServiceImpl(private val user: CurrentUser, private val taskRepository: TaskRepository,
        private val taskListRepository: TaskListRepository) : TaskListService {

    @Transactional
    override fun moveTask(sourceListType: TaskListType, sourceIndex: Int, destinationListType: TaskListType, destinationIndex: Int) {
        val accountId = user.getAccountId()
        if (sourceListType == destinationListType) {
            val list = taskListRepository.findByUserAccountIdAndType(accountId, sourceListType)!!
            val taskId = list.orderedIds[sourceIndex]
            val orderAfterDrag = list.orderedIds.filterIndexed { index, value -> index != sourceIndex }
            val orderAfterDrop = insertBefore(orderAfterDrag, destinationIndex, taskId)
            list.orderedIds = orderAfterDrop
            taskListRepository.save(list)
        } else {
            val sourceList = taskListRepository.findByUserAccountIdAndType(accountId, sourceListType)!!
            val destinationList = taskListRepository.findByUserAccountIdAndType(accountId, destinationListType)!!
            val taskId: Long = sourceList.orderedIds[sourceIndex]
            val sourceOrderAfterDrag = sourceList.orderedIds.filterIndexed { index, value -> index != sourceIndex }
            val destinationOrderAfterDrop = insertBefore(destinationList.orderedIds, destinationIndex, taskId)
            sourceList.orderedIds = sourceOrderAfterDrag
            destinationList.orderedIds = destinationOrderAfterDrop
            taskListRepository.saveAll(listOf(sourceList, destinationList))
            val task = taskRepository.findByIdOrNull(taskId)!!
            task.taskList = destinationList
            taskRepository.save(task)
        }
    }

    @Transactional
    override fun closeTask(taskId: Long): Optional<TaskEntity> {
        val optional = taskRepository.findByIdAndVisibleTrue(taskId)
        return if (optional.isPresent) {
            val task = optional.get()
            val sourceListType = task.taskList!!.type
            val sourceIndex = task.taskList!!.orderedIds.indexOf(task.id)
            moveTask(sourceListType, sourceIndex, CLOSED, 0)
            task.closed = true
            task.closingDate = LocalDateTime.now()
            Optional.of(taskRepository.save(task))
        } else {
            empty()
        }
    }

    @Transactional
    override fun undoCloseTask(taskId: Long): Optional<TaskEntity> {
        val optional = taskRepository.findByIdAndVisibleTrue(taskId)
        return if (optional.isPresent) {
            val task = optional.get()
            val sourceListType = task.taskList!!.type
            val sourceIndex = task.taskList!!.orderedIds.indexOf(task.id)
            moveTask(sourceListType, sourceIndex, INBOX, 0)
            task.closed = false
            task.created = LocalDateTime.now()
            Optional.of(taskRepository.save(task))
        } else {
            empty()
        }
    }
}

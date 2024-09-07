package org.motivepick.service

import org.motivepick.domain.entity.TaskListEntity
import org.motivepick.domain.entity.TaskListType
import org.motivepick.domain.entity.TaskListType.CLOSED
import org.motivepick.domain.entity.TaskListType.INBOX
import org.motivepick.domain.model.TaskList
import org.motivepick.domain.view.TaskView
import org.motivepick.exception.ResourceNotFoundException
import org.motivepick.extensions.ListExtensions.add
import org.motivepick.extensions.TaskEntityExtensions.view
import org.motivepick.extensions.TaskListEntityExtensions.model
import org.motivepick.repository.TaskListRepository
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.motivepick.security.CurrentUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import java.util.Optional.empty
import java.util.concurrent.CountDownLatch

@Service
internal class TaskListServiceImpl(
    private val user: CurrentUser,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val taskListRepository: TaskListRepository
) : TaskListService {

    private val logger: Logger = LoggerFactory.getLogger(TaskListServiceImpl::class.java)

    @Transactional
    override fun createTaskList(): TaskList {
        val accountId = user.getAccountId()
        val user = userRepository.findByAccountId(accountId) ?: throw ResourceNotFoundException("User does not exist, it was deleted or blocked")
        return taskListRepository.save(TaskListEntity(user, TaskListType.CUSTOM, listOf())).model()
    }

    @Transactional
    override fun moveTask(sourceListType: TaskListType, sourceIndex: Int, destinationListType: TaskListType, destinationIndex: Int, requestId: Long, latch: CountDownLatch) {
        val accountId = user.getAccountId()
        if (sourceListType == destinationListType) {
            val list = taskListRepository.findByUserAccountIdAndType(accountId, sourceListType)!!
            val taskId = list.orderedIds[sourceIndex]
            val orderAfterDrag = list.orderedIds.filterIndexed { index, value -> index != sourceIndex }
            val orderAfterDrop = orderAfterDrag.add(destinationIndex, taskId)
            list.orderedIds = orderAfterDrop
            taskListRepository.save(list)
        } else {
            val sourceList = taskListRepository.findByUserAccountIdAndType(accountId, sourceListType)!!
            if (requestId > 0) {
                latch.await()
            }
            val destinationList = taskListRepository.findByUserAccountIdAndType(accountId, destinationListType)!!
            val taskId: Long = sourceList.orderedIds[sourceIndex]
            val sourceOrderAfterDrag = sourceList.orderedIds.filterIndexed { index, value -> index != sourceIndex }
            val destinationOrderAfterDrop = destinationList.orderedIds.add(destinationIndex, taskId)
            sourceList.orderedIds = sourceOrderAfterDrag
            destinationList.orderedIds = destinationOrderAfterDrop
            taskListRepository.saveAll(listOf(sourceList, destinationList))
            val task = taskRepository.findByIdOrNull(taskId)!!
            task.taskList = destinationList
            taskRepository.save(task)
        }
    }

    @Transactional
    override fun closeTask(taskId: Long, requestId: Long, latch: CountDownLatch): Optional<TaskView> {
        val optional = taskRepository.findByIdAndVisibleTrue(taskId)
        return if (optional.isPresent) {
            val task = optional.get()
            val sourceListType = task.taskList!!.type
            val sourceIndex = task.taskList!!.orderedIds.indexOf(task.id)
            moveTask(sourceListType, sourceIndex, CLOSED, 0, requestId, latch)
            task.closed = true
            task.closingDate = LocalDateTime.now()
            logger.info("Closed task with ID {}", task.id)
            Optional.of(taskRepository.save(task).view())
        } else {
            empty()
        }
    }

    @Transactional
    override fun reopenTask(taskId: Long, requestId: Long, latch: CountDownLatch): Optional<TaskView> {
        val optional = taskRepository.findByIdAndVisibleTrue(taskId)
        return if (optional.isPresent) {
            val task = optional.get()
            val sourceListType = task.taskList!!.type
            val sourceIndex = task.taskList!!.orderedIds.indexOf(task.id)
            moveTask(sourceListType, sourceIndex, INBOX, 0, requestId, latch)
            task.closed = false
            task.created = LocalDateTime.now()
            logger.info("Reopened task with ID {}", task.id)
            Optional.of(taskRepository.save(task).view())
        } else {
            empty()
        }
    }
}

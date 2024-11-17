package org.motivepick.service

import org.motivepick.domain.entity.TaskEntity
import org.motivepick.domain.entity.TaskListEntity
import org.motivepick.domain.entity.TaskListType
import org.motivepick.domain.entity.TaskListType.*
import org.motivepick.domain.model.TaskList
import org.motivepick.domain.view.TaskView
import org.motivepick.exception.ResourceNotFoundException
import org.motivepick.extensions.ClockExtensions.endOfToday
import org.motivepick.extensions.CurrentUserExtensions.owns
import org.motivepick.extensions.ListExtensions.add
import org.motivepick.extensions.TaskEntityExtensions.view
import org.motivepick.extensions.TaskListEntityExtensions.model
import org.motivepick.repository.TaskListRepository
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.motivepick.security.CurrentUser
import org.motivepick.security.UserNotAuthorizedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Optional.empty
import java.util.concurrent.CountDownLatch
import kotlin.jvm.optionals.getOrNull

@Service
internal class TaskListServiceImpl(
    private val currentUser: CurrentUser,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val taskListRepository: TaskListRepository
) : TaskListService {

    private val logger: Logger = LoggerFactory.getLogger(TaskListServiceImpl::class.java)

    @Transactional
    override fun createTaskList(): TaskList {
        val accountId = currentUser.getAccountId()
        val user = userRepository.findByAccountId(accountId) ?: throw ResourceNotFoundException("User does not exist, it was deleted or blocked")
        return taskListRepository.save(TaskListEntity(user, CUSTOM, listOf())).model()
    }

    @Transactional
    override fun moveTask(sourceListId: String, taskId: Long, destinationListId: String, destinationIndex: Int, requestId: Long, latch: CountDownLatch): TaskView {
        val sourceListType = tryParse(sourceListId)
        val destinationListType = tryParse(destinationListId)
        if (sourceListType == SCHEDULE_SECTION || destinationListType == SCHEDULE_SECTION) {
            val task = taskRepository.findByIdAndVisibleTrue(taskId).getOrNull()
            if (task == null) {
                throw ResourceNotFoundException("Task with ID $taskId not found")
            } else if (currentUser.owns(task)) {
                task.dueDate = dueDateFromScheduleSection(destinationListId)
                taskRepository.save(task)
                return task.view()
            } else {
                throw UserNotAuthorizedException("The user does not own the task")
            }
        } else {
            return moveTask(sourceListType, taskId, destinationListType, destinationIndex, requestId, latch).view()
        }
    }

    private fun dueDateFromScheduleSection(destinationListId: String): LocalDateTime {
        if (destinationListId == "future") {
            return Clock.system(ZoneOffset.UTC)
                .endOfToday()
                .plusWeeks(1)
                .toLocalDateTime()
        }
        return OffsetDateTime.parse(destinationListId, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .atZoneSameInstant(ZoneOffset.UTC)
            .toLocalDateTime()
    }

    private fun tryParse(listType: String): TaskListType {
        return try {
            TaskListType.valueOf(listType)
        } catch (e: IllegalArgumentException) {
            SCHEDULE_SECTION
        }
    }

    private fun moveTask(sourceListType: TaskListType, taskId: Long, destinationListType: TaskListType, destinationIndex: Int, requestId: Long, latch: CountDownLatch): TaskEntity {
        val accountId = currentUser.getAccountId()
        val task = taskRepository.findByIdOrNull(taskId)!!
        if (sourceListType == destinationListType) {
            val list = taskListRepository.findByUserAccountIdAndType(accountId, sourceListType)!!
            if (requestId > 0) {
                latch.await()
            }
            val orderAfterDrag = list.orderedIds.filterIndexed { _, value -> value != taskId }
            val orderAfterDrop = orderAfterDrag.add(destinationIndex, taskId)
            list.orderedIds = orderAfterDrop
            taskListRepository.save(list)
        } else {
            val sourceList = taskListRepository.findByUserAccountIdAndType(accountId, sourceListType)!!
            if (requestId > 0) {
                latch.await()
            }
            val destinationList = taskListRepository.findByUserAccountIdAndType(accountId, destinationListType)!!
            val sourceOrderAfterDrag = sourceList.orderedIds.filterIndexed { _, value -> value != taskId }
            val destinationOrderAfterDrop = destinationList.orderedIds.add(destinationIndex, taskId)
            sourceList.orderedIds = sourceOrderAfterDrag
            destinationList.orderedIds = destinationOrderAfterDrop
            taskListRepository.saveAll(listOf(sourceList, destinationList))
            task.taskList = destinationList
            taskRepository.save(task)
        }
        return task
    }

    @Transactional
    override fun closeTask(taskId: Long, requestId: Long, latch: CountDownLatch): Optional<TaskView> {
        val optional = taskRepository.findByIdAndVisibleTrue(taskId)
        return if (optional.isPresent) {
            val task = optional.get()
            val sourceListType = task.taskList!!.type
            moveTask(sourceListType, task.id, CLOSED, 0, requestId, latch)
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
            moveTask(sourceListType, task.id, INBOX, 0, requestId, latch)
            task.closed = false
            task.created = LocalDateTime.now()
            logger.info("Reopened task with ID {}", task.id)
            Optional.of(taskRepository.save(task).view())
        } else {
            empty()
        }
    }
}

package org.motivepick.service

import org.motivepick.domain.entity.TaskListType
import org.motivepick.repository.TaskListRepository
import org.motivepick.security.CurrentUser
import org.motivepick.service.Lists.insertBefore
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TaskListServiceImpl(private val user: CurrentUser, private val taskListRepository: TaskListRepository) : TaskListService {

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
            TODO("Not implemented yet")
        }
    }
}

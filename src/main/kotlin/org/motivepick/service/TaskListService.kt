package org.motivepick.service

import org.motivepick.domain.entity.TaskEntity
import org.motivepick.domain.entity.TaskListType
import java.util.*

interface TaskListService {

    fun moveTask(sourceListType: TaskListType, sourceIndex: Int, destinationListType: TaskListType, destinationIndex: Int)

    fun closeTask(taskId: Long): Optional<TaskEntity>

    fun undoCloseTask(taskId: Long): Optional<TaskEntity>
}

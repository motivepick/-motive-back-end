package org.motivepick.service

import org.motivepick.domain.entity.Task
import org.motivepick.domain.entity.TaskListType
import java.util.*

interface TaskListService {

    fun moveTask(sourceListType: TaskListType, sourceIndex: Int, destinationListType: TaskListType, destinationIndex: Int)

    fun closeTask(taskId: Long): Optional<Task>

    fun undoCloseTask(taskId: Long): Optional<Task>
}

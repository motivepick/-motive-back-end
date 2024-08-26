package org.motivepick.service

import org.motivepick.domain.entity.TaskListType
import org.motivepick.domain.model.TaskList
import org.motivepick.domain.view.TaskView
import java.util.*

interface TaskListService {

    fun createTaskList(): TaskList

    fun moveTask(sourceListType: TaskListType, sourceIndex: Int, destinationListType: TaskListType, destinationIndex: Int)

    fun closeTask(taskId: Long): Optional<TaskView>

    fun reopenTask(taskId: Long): Optional<TaskView>
}

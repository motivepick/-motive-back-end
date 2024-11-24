package org.motivepick.service

import org.motivepick.domain.entity.TaskListType
import org.motivepick.domain.model.TaskList
import org.motivepick.domain.view.TaskView
import java.util.*
import java.util.concurrent.CountDownLatch

interface TaskListService {

    fun createTaskList(): TaskList

    fun moveTask(sourceListId: TaskListType, taskId: Long, destinationListId: TaskListType, destinationIndex: Int, requestId: Long = 0, latch: CountDownLatch = CountDownLatch(0)): TaskView

    fun closeTask(taskId: Long, requestId: Long = 0, latch: CountDownLatch = CountDownLatch(0)): Optional<TaskView>

    fun reopenTask(taskId: Long, requestId: Long = 0, latch: CountDownLatch = CountDownLatch(0)): Optional<TaskView>
}

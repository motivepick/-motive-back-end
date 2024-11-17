package org.motivepick.service

import org.motivepick.domain.model.TaskList
import org.motivepick.domain.view.TaskView
import java.util.*
import java.util.concurrent.CountDownLatch

interface TaskListService {

    fun createTaskList(): TaskList

    fun moveTask(sourceListId: String, taskId: Long, destinationListId: String, destinationIndex: Int, requestId: Long = 0, latch: CountDownLatch = CountDownLatch(0)): TaskView

    fun closeTask(taskId: Long, requestId: Long = 0, latch: CountDownLatch = CountDownLatch(0)): Optional<TaskView>

    fun reopenTask(taskId: Long, requestId: Long = 0, latch: CountDownLatch = CountDownLatch(0)): Optional<TaskView>
}

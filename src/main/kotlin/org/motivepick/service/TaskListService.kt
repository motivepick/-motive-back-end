package org.motivepick.service

import org.motivepick.domain.entity.TaskListType

interface TaskListService {

    fun moveTask(sourceListType: TaskListType, sourceIndex: Int, destinationListType: TaskListType, destinationIndex: Int)
}
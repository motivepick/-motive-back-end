package org.motivepick.domain.ui.task

import org.motivepick.domain.entity.TaskListType

class MoveTaskRequest {

    var sourceListType: TaskListType? = null
    var sourceIndex: Int? = null
    var destinationListType: TaskListType? = null
    var destinationIndex: Int? = null
}

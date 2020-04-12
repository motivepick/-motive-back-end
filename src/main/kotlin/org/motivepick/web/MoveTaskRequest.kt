package org.motivepick.web

import org.motivepick.domain.entity.TaskListType

class MoveTaskRequest {

    var sourceListType: TaskListType? = null
    var sourceIndex: Int? = null
    var destinationListType: TaskListType? = null
    var destinationIndex: Int? = null
}

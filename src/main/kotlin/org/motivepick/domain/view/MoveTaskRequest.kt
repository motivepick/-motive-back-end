package org.motivepick.domain.view

import org.motivepick.domain.entity.TaskListType

class MoveTaskRequest {

    var sourceListId: TaskListType? = null
    var taskId: Long? = null
    var destinationListId: TaskListType? = null
    var destinationIndex: Int? = null
}

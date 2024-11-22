package org.motivepick.domain.view

class MoveTaskRequest {

    var sourceListId: String = ""
    var taskId: Long? = null
    var destinationListId: String = ""
    var destinationIndex: Int? = null
}

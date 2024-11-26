package org.motivepick.domain.view

import java.time.LocalDateTime

class RescheduleTaskRequest {

    /**
     * The new due date of the task. Can be null if there is no need to set a new due date
     * (e.g., when the task is moved within the same day).
     */
    var dueDate: LocalDateTime? = null
    var taskIds: List<Long> = emptyList()
}

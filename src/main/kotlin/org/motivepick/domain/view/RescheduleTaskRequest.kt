package org.motivepick.domain.view

import java.time.LocalDateTime

class RescheduleTaskRequest {

    var dueDate: LocalDateTime? = null
    var taskIds: List<Long> = emptyList()
}

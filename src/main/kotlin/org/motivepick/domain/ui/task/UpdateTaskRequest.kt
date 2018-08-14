package org.motivepick.domain.ui.task

import java.time.LocalDateTime

class UpdateTaskRequest {

    var name: String? = null
    var description: String? = null
    var dueDate: LocalDateTime? = null
    var closed: Boolean? = null
}
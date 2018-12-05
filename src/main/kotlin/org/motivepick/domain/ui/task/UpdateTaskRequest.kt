package org.motivepick.domain.ui.task

import java.time.LocalDateTime

class UpdateTaskRequest {

    var name: String? = null
    var description: String? = null
    var created: LocalDateTime? = null
    var dueDate: String? = null
    var closingDate: LocalDateTime? = null
    var closed: Boolean? = null
}

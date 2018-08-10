package org.motivepick.domain.ui

import java.time.LocalDateTime

class UpdateGoalRequest {

    var name: String? = null
    var description: String? = null
    val dueDate: LocalDateTime? = null
    var closed: Boolean? = null
}

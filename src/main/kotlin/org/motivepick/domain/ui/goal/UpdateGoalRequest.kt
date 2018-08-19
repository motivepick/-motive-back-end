package org.motivepick.domain.ui.goal

import java.time.LocalDateTime

class UpdateGoalRequest {

    var name: String? = null
    var description: String? = null
    var dueDate: LocalDateTime? = null
    var closed: Boolean? = null
    var colorTag: String? = null
}

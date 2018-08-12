package org.motivepick.domain.ui.goal

import com.fasterxml.jackson.annotation.JsonCreator
import java.time.LocalDateTime

data class CreateGoalRequest @JsonCreator constructor(val accountId: Long, val name: String) {

    var description: String? = null
    var dueDate: LocalDateTime? = null
}
package org.motivepick.domain.ui

import com.fasterxml.jackson.annotation.JsonCreator
import java.time.LocalDateTime

data class CreateGoalRequest @JsonCreator constructor(val userId: String, val name: String) {

    var description: String? = null
    var dueDate: LocalDateTime? = null
}
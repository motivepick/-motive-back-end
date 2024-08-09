package org.motivepick.domain.ui.task

import com.fasterxml.jackson.annotation.JsonCreator
import java.time.LocalDateTime

data class CreateTaskRequest @JsonCreator constructor(val name: String) {

    var description: String? = null
    var dueDate: LocalDateTime? = null
}
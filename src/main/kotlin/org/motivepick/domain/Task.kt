package org.motivepick.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

@Document
class Task {

    @Id
    var id: String? = null
    @NotBlank
    var userId: String? = null
    var name: String? = null
    var description: String? = null
    var instantOfCreation: LocalDateTime? = null
    var dueDate: LocalDateTime? = null
    var closed: Boolean = false

    @DBRef(lazy = true)
    var subtasks: MutableList<Task> = ArrayList()
}

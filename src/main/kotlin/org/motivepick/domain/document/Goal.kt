package org.motivepick.domain.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

@Document
open class Goal {

    @Id
    var id: String? = null
    var userId: String? = null
    var name: String? = null
    var description: String? = null
    var instantOfCreation: LocalDateTime = LocalDateTime.now(UTC)
    var dueDate: LocalDateTime? = null
    var closed: Boolean = false

    @DBRef(lazy = true)
    var tasks: MutableList<Task> = ArrayList()
}
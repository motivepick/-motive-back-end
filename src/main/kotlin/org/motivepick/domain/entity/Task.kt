package org.motivepick.domain.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.persistence.*

@Entity
class Task(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "USER_ID", nullable = false)
        var user: User,

        @Column(nullable = false)
        var name: String) : AbstractEntity() {

    var description: String? = null

    @Column(nullable = false)
    var created: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

    var dueDate: LocalDateTime? = null
    var closed: Boolean = false

    @JsonIgnore // TODO: don't mix persistent and REST layers. The annotation should be removed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GOAL_ID")
    var goal: Goal? = null
}

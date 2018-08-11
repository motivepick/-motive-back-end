package org.motivepick.domain.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import javax.persistence.*

@Entity
class Goal(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "USER_ID", nullable = false)
        var user: User,

        @Column(nullable = false)
        var name: String) : AbstractEntity() {

    var description: String? = null

    @Column(nullable = false)
    var created: LocalDateTime = LocalDateTime.now(UTC)

    var dueDate: LocalDateTime? = null
    var closed: Boolean = false

    @JsonIgnore // TODO: don't mix persistent and REST layers. The annotation should be removed
    @OneToMany(mappedBy = "goal", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var tasks: MutableList<Task> = ArrayList()
}
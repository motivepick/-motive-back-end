package org.motivepick.domain.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.LAZY
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "TASK")
class TaskEntity(
        @JsonIgnore
        @ManyToOne(fetch = LAZY)
        @JoinColumn(name = "USER_ID", nullable = false)
        var user: UserEntity,

        @Column(nullable = false)
        var name: String) : AbstractEntity() {
    constructor() : this(UserEntity(), "")

    var description: String? = null

    @Column(nullable = false)
    var created: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

    var dueDate: LocalDateTime? = null
    var closed: Boolean = false

    var closingDate: LocalDateTime? = null

    @Column(nullable = false)
    var visible: Boolean = true

    @JsonIgnore // TODO: don't mix persistent and REST layers. The annotation should be removed. The same for above
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "TASK_LIST_ID")
    var taskList: TaskListEntity? = null
}

package org.motivepick.domain.entity

import java.time.LocalDateTime
import java.time.ZoneOffset
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity(name = "TASK")
class TaskEntity(
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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "TASK_LIST_ID")
    var taskList: TaskListEntity? = null
}

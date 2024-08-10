package org.motivepick.domain.entity

import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.FetchType.LAZY
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.util.Assert.notNull

@Entity(name = "TASK_LIST")
class TaskListEntity(
        @ManyToOne(fetch = LAZY)
        @JoinColumn(name = "USER_ID", nullable = false)
        var user: UserEntity,

        @Column(name = "TASK_LIST_TYPE", nullable = false)
        @Enumerated(STRING)
        var type: TaskListType,

        @JdbcTypeCode(SqlTypes.JSON)
        @Column(name = "ORDERED_TASK_IDS", nullable = false)
        var orderedIds: List<Long>) : AbstractEntity() {

    constructor() : this(UserEntity(), TaskListType.INBOX, emptyList())

    @OneToMany(mappedBy = "taskList", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var tasks: MutableList<TaskEntity> = ArrayList()

    fun addTask(task: TaskEntity) {
        notNull(task.id, "Task ID must be present if you want to add the task to the task list")
        task.taskList = this
        tasks.add(task)
        orderedIds = listOf(task.id) + orderedIds
    }
}

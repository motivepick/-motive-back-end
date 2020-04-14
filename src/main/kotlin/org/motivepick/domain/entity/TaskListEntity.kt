package org.motivepick.domain.entity

import com.vladmihalcea.hibernate.type.array.ListArrayType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.springframework.util.Assert.notNull
import javax.persistence.*
import javax.persistence.CascadeType.ALL
import javax.persistence.EnumType.STRING
import javax.persistence.FetchType.LAZY

@Entity(name = "TASK_LIST")
@TypeDef(name = "ListArray", typeClass = ListArrayType::class)
class TaskListEntity(
        @ManyToOne(fetch = LAZY)
        @JoinColumn(name = "USER_ID", nullable = false)
        var user: User,

        @Column(name = "TASK_LIST_TYPE", nullable = false)
        @Enumerated(STRING)
        var type: TaskListType,

        @Type(type = "ListArray")
        @Column(name = "ORDERED_TASK_IDS", nullable = false, columnDefinition = "BIGINT[]")
        var orderedIds: List<Long?>) : AbstractEntity() {

    @OneToMany(mappedBy = "taskList", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var tasks: MutableList<Task> = ArrayList()

    fun addTask(task: Task) {
        notNull(task.id, "Task ID must be present if you want to add the task to the task list")
        task.taskList = this
        tasks.add(task)
        orderedIds = listOf(task.id) + orderedIds
    }
}

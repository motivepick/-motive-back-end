package org.motivepick.domain.entity

import javax.persistence.*
import javax.persistence.CascadeType.ALL
import javax.persistence.EnumType.STRING
import javax.persistence.FetchType.LAZY

@Entity(name = "TASK_LIST")
class TaskListEntity(
        @ManyToOne(fetch = LAZY)
        @JoinColumn(name = "USER_ID", nullable = false)
        var user: User,

        @Column(name = "TASK_LIST_TYPE", nullable = false)
        @Enumerated(STRING)
        var type: TaskListType) : AbstractEntity() {

    @OneToMany(mappedBy = "taskList", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var tasks: MutableList<Task> = ArrayList()

    fun addTask(task: Task) {
        task.taskList = this
        tasks.add(task)
    }
}

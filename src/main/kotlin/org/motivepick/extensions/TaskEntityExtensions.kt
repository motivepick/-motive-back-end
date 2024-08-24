package org.motivepick.extensions

import org.motivepick.domain.entity.TaskEntity
import org.motivepick.domain.model.Task
import org.motivepick.domain.view.TaskView
import java.time.ZoneOffset

internal object TaskEntityExtensions {

    fun TaskEntity.model(): Task = Task(this.id, this.name, this.description ?: "", this.dueDate, this.closed)

    fun TaskEntity.view(): TaskView = TaskView(this.id, this.name, this.description ?: "", this.dueDate?.atOffset(ZoneOffset.UTC), this.closed)
}

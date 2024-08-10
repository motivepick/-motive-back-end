package org.motivepick.service

import org.motivepick.domain.entity.TaskEntity
import org.motivepick.domain.view.TaskView

object TaskEntityExtensions {

    fun TaskEntity.view(): TaskView = TaskView(this.id, this.name, this.description ?: "", this.dueDate, this.closed)
}

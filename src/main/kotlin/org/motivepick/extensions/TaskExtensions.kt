package org.motivepick.extensions

import org.motivepick.domain.model.Task
import org.motivepick.domain.view.TaskView
import java.time.ZoneOffset

internal object TaskExtensions {

    fun Task.view(): TaskView = TaskView(this.id, this.name, this.description, this.dueDate?.atOffset(ZoneOffset.UTC), this.closed)
}

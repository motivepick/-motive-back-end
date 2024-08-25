package org.motivepick.extensions

import org.motivepick.domain.model.ScheduledTask
import org.motivepick.domain.view.ScheduledTaskView
import java.time.ZoneOffset

object ScheduledTaskExtensions {

    fun ScheduledTask.view(): ScheduledTaskView =
        ScheduledTaskView(this.id, this.name, this.description, this.dueDate.atOffset(ZoneOffset.UTC), this.closed)
}

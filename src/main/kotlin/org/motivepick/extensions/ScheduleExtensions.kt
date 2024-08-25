package org.motivepick.extensions

import org.motivepick.domain.model.Schedule
import org.motivepick.domain.view.ScheduleView
import org.motivepick.extensions.ScheduledTaskExtensions.view

internal object ScheduleExtensions {

    fun Schedule.view(): ScheduleView =
        ScheduleView(week.mapValues { entity -> entity.value.map { it.view() } }, overdue.map { it.view() }, future.map { it.view() })
}

package org.motivepick.extensions

import org.motivepick.domain.model.Schedule
import org.motivepick.domain.view.ScheduleView
import org.motivepick.extensions.TaskExtensions.view
import java.time.ZoneOffset

object ScheduleExtensions {

    fun Schedule.view(): ScheduleView =
        ScheduleView(week.map { (k, v) -> k.atOffset(ZoneOffset.UTC) to v.map { it.view() } }.toMap(), overdue.map { it.view() }, future.map { it.view() })
}

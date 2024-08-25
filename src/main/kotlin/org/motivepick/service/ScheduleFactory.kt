package org.motivepick.service

import org.motivepick.domain.model.Schedule
import org.motivepick.domain.model.ScheduledTask
import org.motivepick.extensions.ClockExtensions.endOfToday
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit.DAYS

@Component
class ScheduleFactory {

    fun scheduleFor(tasksWithDueDate: List<ScheduledTask>, timeZone: ZoneId): Schedule {
        val endOfToday = Clock.system(timeZone).endOfToday()
        val week: Map<ZonedDateTime, List<ScheduledTask>> = weekDays(endOfToday)
            .associateWith { endOfDay -> tasksWithDueDate.filter { it.dueDate.atZone(timeZone) in startOfDayFrom(endOfDay)..endOfDay } }
        val startOfToday = startOfDayFrom(endOfToday)
        val overdue = tasksWithDueDate.filter { it.dueDate.atZone(timeZone).isBefore(startOfToday) }
        val endOfWeek = endOfToday.plusDays(6)
        val futureTasks = tasksWithDueDate.filter { it.dueDate.atZone(timeZone).isAfter(endOfWeek) }.sortedBy { it.dueDate }
        return Schedule(week, overdue, futureTasks)
    }

    private fun weekDays(endOfToday: ZonedDateTime): List<ZonedDateTime> = (0..6)
        .map { it.toLong() }
        .map { endOfToday.plusDays(it) }

    private fun startOfDayFrom(dateTime: ZonedDateTime): ZonedDateTime = dateTime.truncatedTo(DAYS)
}

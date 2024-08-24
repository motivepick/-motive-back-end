package org.motivepick.service

import org.motivepick.domain.model.Schedule
import org.motivepick.domain.model.Task
import org.motivepick.extensions.LocalDateTimeExtensions.isSameDayAs
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime.MAX
import kotlin.collections.set

@Component
class ScheduleFactory(private val clock: Clock) {

    fun scheduleFor(tasksWithDueDate: List<Task>): Schedule {
        val week: MutableMap<LocalDateTime, List<Task>> = week()
        for (dayOfWeek in week.keys) {
            val tasksOfTheDay = tasksWithDueDate.filter { dayOfWeek.isSameDayAs(it.dueDate!!) }
            week[dayOfWeek] = tasksOfTheDay
        }
        val startOfToday = LocalDate.now(clock).atStartOfDay()
        val overdue = tasksWithDueDate.filter { it.dueDate!!.isBefore(startOfToday) }

        val startOfFuture = startOfToday.plusDays(7)

        return tasksWithDueDate.asSequence()
            .filter { it.dueDate!!.isAfter(startOfFuture) }
            .sortedBy { it.dueDate }
            .firstOrNull()
            ?.let { task -> tasksWithDueDate.filter { task.dueDate!!.isSameDayAs(it.dueDate!!) } }
            ?.let { Schedule(week, overdue, it) }
            ?: Schedule(week, overdue, listOf())
    }

    private fun week(): MutableMap<LocalDateTime, List<Task>> =
        LocalDate
            .now(clock)
            .atTime(MAX)
            .let { endOfToday ->
                (0..6)
                    .map { it.toLong() }
                    .map { endOfToday.plusDays(it) }
                    .associateBy({ it }, { ArrayList<Task>() })
                    .toMutableMap()
            }
}

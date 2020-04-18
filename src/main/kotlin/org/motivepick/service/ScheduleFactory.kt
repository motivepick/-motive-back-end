package org.motivepick.service

import org.motivepick.domain.entity.Task
import org.motivepick.domain.model.Schedule
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime.MAX
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.collections.set

@Component
class ScheduleFactory(private val clock: Clock) {

    fun scheduleFor(tasksWithDueDate: List<Task>): Schedule {
        val week: MutableMap<LocalDateTime, List<Task>> = week()
        for (dayOfWeek in week.keys) {
            val tasksOfTheDay = tasksWithDueDate.filter { areTheSameDay(dayOfWeek, it.dueDate!!) }
            week[dayOfWeek] = tasksOfTheDay
        }
        val startOfToday = LocalDate.now(clock).atStartOfDay()
        val overdue = tasksWithDueDate.filter { it.dueDate!!.isBefore(startOfToday) }

        val startOfFuture = startOfToday.plusDays(7)

        val firstFutureTaskOrNull = tasksWithDueDate.asSequence()
                .filter { it.dueDate!!.isAfter(startOfFuture) }
                .sortedBy { it.dueDate }
                .firstOrNull()

        return if (firstFutureTaskOrNull == null) {
            Schedule(week, overdue, listOf())
        } else {
            val futureTasks = tasksWithDueDate.filter { areTheSameDay(firstFutureTaskOrNull.dueDate!!, it.dueDate!!) }
            Schedule(week, overdue, futureTasks)
        }
    }

    private fun areTheSameDay(day: LocalDateTime, dueDate: LocalDateTime): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return day.format(formatter) == dueDate.format(formatter)
    }

    private fun week(): MutableMap<LocalDateTime, List<Task>> {
        val schedule: MutableMap<LocalDateTime, List<Task>> = LinkedHashMap()
        val endOfToday = LocalDate.now(clock).atTime(MAX)
        for (i in 0..6) {
            schedule[endOfToday.plusDays(i.toLong())] = ArrayList()
        }
        return schedule
    }
}

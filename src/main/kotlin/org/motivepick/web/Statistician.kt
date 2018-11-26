package org.motivepick.web

import org.motivepick.domain.entity.Task
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.LocalTime
import java.time.ZoneOffset.UTC
import java.util.*

@Component
class Statistician {

    fun calculateStatisticsFor(tasks: List<Task>): Statistics {
        val startOfToday = startOfToday()
        val endOfToday = endOfToday()
        val openTasks = tasks.filterNot { t -> t.closed }
        val closedTasks = tasks.filter { t -> t.closed }
        val overdueTasks = openTasks.filter { t -> Objects.nonNull(t.dueDate) && t.dueDate!!.isBefore(startOfToday) }
        val isDueToday: (Task) -> Boolean = { t ->
            Objects.nonNull(t.dueDate) && startOfToday!!.isBefore(t.dueDate) && t.dueDate!!.isBefore(endOfToday)
        }
        val openTasksDueToday = openTasks.filter(isDueToday)
        val closedTasksDueToday = closedTasks.filter(isDueToday)
        val futureTasks = openTasks.filter { t -> Objects.nonNull(t.dueDate) && t.dueDate!!.isAfter(endOfToday) }
        val nextDueDate = if (futureTasks.isEmpty()) null else futureTasks.sortedWith(compareBy { it.dueDate }).first().dueDate
        return Statistics(openTasks.size, closedTasks.size, overdueTasks.size,
                openTasksDueToday.size, closedTasksDueToday.size, nextDueDate)
    }

    private fun startOfToday(): LocalDateTime? = now(UTC).toLocalDate().atStartOfDay(UTC).toLocalDateTime()

    private fun endOfToday(): LocalDateTime? = now(UTC).toLocalDate().atTime(LocalTime.MAX)
}

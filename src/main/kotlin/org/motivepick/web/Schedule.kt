package org.motivepick.web

import org.motivepick.domain.entity.Task
import java.time.LocalDateTime

class Schedule(val week: Map<LocalDateTime, List<Task>>, val overdue: List<Task>, val future: List<Task>) {

    override fun toString(): String = "Schedule(week=$week, overdue=$overdue, future=$future)"
}

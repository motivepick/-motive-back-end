package org.motivepick.domain.model

import org.motivepick.domain.entity.TaskEntity
import java.time.LocalDateTime

class Schedule(val week: Map<LocalDateTime, List<TaskEntity>>, val overdue: List<TaskEntity>, val future: List<TaskEntity>) {

    override fun toString(): String = "Schedule(week=$week, overdue=$overdue, future=$future)"
}

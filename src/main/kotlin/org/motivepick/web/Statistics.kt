package org.motivepick.web

import java.time.LocalDateTime

class Statistics(val totalOpenTasks: Int, val totalClosedTasks: Int, val totalOverdueTasks: Int, val openTasksDueToday: Int,
        val closedTasksDueToday: Int, val nextDueDate: LocalDateTime)

package org.motivepick.domain.model

import java.time.LocalDateTime

data class Schedule(val week: Map<LocalDateTime, List<Task>>, val overdue: List<Task>, val future: List<Task>)

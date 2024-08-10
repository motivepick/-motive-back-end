package org.motivepick.domain.view

import java.time.LocalDateTime

data class ScheduleView(val week: Map<LocalDateTime, List<TaskView>>, val overdue: List<TaskView>, val future: List<TaskView>)

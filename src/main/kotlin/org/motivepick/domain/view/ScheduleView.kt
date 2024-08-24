package org.motivepick.domain.view

import java.time.OffsetDateTime

data class ScheduleView(val week: Map<OffsetDateTime, List<TaskView>>, val overdue: List<TaskView>, val future: List<TaskView>)

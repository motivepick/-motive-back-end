package org.motivepick.domain.view

import java.time.ZonedDateTime

data class ScheduleView(val week: Map<ZonedDateTime, List<ScheduledTaskView>>, val overdue: List<ScheduledTaskView>, val future: List<ScheduledTaskView>)

package org.motivepick.domain.model

import java.time.ZonedDateTime

data class Schedule(val week: Map<ZonedDateTime, List<ScheduledTask>>, val overdue: List<ScheduledTask>, val future: List<ScheduledTask>)

package org.motivepick.extensions

import java.time.Clock
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit.DAYS

object ClockExtensions {

    fun Clock.endOfToday(): ZonedDateTime = ZonedDateTime.now(this).truncatedTo(DAYS).plusDays(1).minusNanos(1)
}

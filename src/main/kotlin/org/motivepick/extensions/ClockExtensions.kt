package org.motivepick.extensions

import java.time.Clock
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit.DAYS

internal object ClockExtensions {

    /**
     * Note: this is actually not the latest possible time of today. The latest possible time would be if we subtracted 1 nanosecond, not 1 millisecond.
     * However, some databases (like PostgreSQL) and some JavaScript libraries cannot provide enough precision to represent the 1 nanosecond difference.
     */
    fun Clock.endOfToday(): ZonedDateTime = ZonedDateTime.now(this).truncatedTo(DAYS).plusDays(1).minus(Duration.ofMillis(1))
}

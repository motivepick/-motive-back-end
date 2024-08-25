package org.motivepick.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.motivepick.extensions.ClockExtensions.endOfToday
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ClockExtensionsTest {

    @Test
    fun `endOfToday should return the end of the day in the given zone`() {
        val clock = Clock.fixed(Instant.parse("2024-08-25T11:52:00.00Z"), ZoneId.of("GMT+3"))
        assertThat(clock.endOfToday()).isEqualTo(ZonedDateTime.parse("2024-08-25T23:59:59.999999999+03:00"))
    }
}

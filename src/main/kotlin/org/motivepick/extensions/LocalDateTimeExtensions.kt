package org.motivepick.extensions

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal object LocalDateTimeExtensions {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    fun LocalDateTime.isSameDayAs(other: LocalDateTime): Boolean =
        this.format(formatter) == other.format(formatter)
}

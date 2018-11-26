package org.motivepick.web

import org.junit.Assert.assertEquals
import org.junit.Test
import org.motivepick.domain.entity.Task
import org.motivepick.domain.entity.User
import java.time.Clock
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset.UTC

class ScheduleFactoryTest {

    @Test
    fun test() {
        val clock = Clock.fixed(LocalDateTime.of(2018, 11, 25, 11, 32).toInstant(UTC), UTC)
        val yesterday = LocalDateTime.of(2018, 11, 24, 23, 59)
        val tomorrow = LocalDateTime.of(2018, 11, 26, 17, 35)
        val december2nd = LocalDateTime.of(2018, 12, 2, 17, 35)
        val anotherDecember2nd = LocalDateTime.of(2018, 12, 2, 17, 35)
        val december3rd = LocalDateTime.of(2018, 12, 3, 17, 35)
        val dayAfterTomorrow = LocalDateTime.of(2018, 11, 27, 23, 59)
        val schedule = ScheduleFactory(clock).scheduleFor(listOf(task(dayAfterTomorrow),
                task(tomorrow), task(dayAfterTomorrow), task(december2nd), task(december3rd), task(anotherDecember2nd), task(yesterday)))
        assertEquals(1, schedule.overdue.size)
        assertEquals(2, schedule.future.size)
        assertEquals(1, schedule.week[tomorrow.toLocalDate().atTime(LocalTime.MAX)]!!.size)
    }

    private fun task(dueDate: LocalDateTime?): Task {
        val task = Task(User(1447751358702195, "Sergey Yaskov"), "Cleanup database")
        task.dueDate = dueDate
        return task
    }
}

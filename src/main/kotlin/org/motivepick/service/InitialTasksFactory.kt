package org.motivepick.service

import org.motivepick.domain.entity.Task
import org.motivepick.domain.entity.User
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.ZoneOffset.UTC

@Component
class InitialTasksFactory {

    fun createInitialTasks(tasksOwner: User): List<Task> {
        val now = now(UTC)
        val yesterday = now.minusDays(1)
        val tomorrow = now.plusDays(1)
        val dayAfterTomorrow = now.plusDays(2)
        val inTwoMonths = now.plusMonths(2)
        return listOf(task(tasksOwner, "Find a hotel in Sofia", false),
                task(tasksOwner, "Write a review for the Estonian teacher", yesterday, false),
                task(tasksOwner, "Buy a birthday present for Steve", tomorrow, false),
                task(tasksOwner, "Finish the course about microservices", false),
                task(tasksOwner, "Finalize the blog post", dayAfterTomorrow, false),
                task(tasksOwner, "Tidy up the kitchen", yesterday, false),
                task(tasksOwner, "Transfer money for the new illustration to Ann", "12$ for each illustration should be transferred. 36$ in total.", inTwoMonths))
    }

    private fun task(tasksOwner: User, name: String, closed: Boolean): Task {
        val task = Task(tasksOwner, name)
        task.closed = closed
        return task
    }

    private fun task(tasksOwner: User, name: String, dueDate: LocalDateTime, closed: Boolean): Task {
        val task = task(tasksOwner, name, closed)
        task.dueDate = dueDate
        return task
    }

    private fun task(tasksOwner: User, name: String, description: String, dueDate: LocalDateTime): Task {
        val task = task(tasksOwner, name, dueDate, false)
        task.description = description
        return task
    }
}

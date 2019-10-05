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
        return listOf(task(tasksOwner, "Buy a birthday present for Steve", tomorrow),
                task(tasksOwner, "Finish the course about microservices"),
                task(tasksOwner, "Finalize the blog post", dayAfterTomorrow),
                task(tasksOwner, "Tidy up the kitchen", yesterday),
                task(tasksOwner, "Transfer money for the new illustration to Ann", "12$ for each illustration should be transferred. 36$ in total.", inTwoMonths))
    }

    private fun task(tasksOwner: User, name: String) = Task(tasksOwner, name)

    private fun task(tasksOwner: User, name: String, dueDate: LocalDateTime): Task {
        val task = Task(tasksOwner, name)
        task.dueDate = dueDate
        return task
    }

    private fun task(tasksOwner: User, name: String, description: String, dueDate: LocalDateTime): Task {
        val task = task(tasksOwner, name, dueDate)
        task.description = description
        return task
    }
}

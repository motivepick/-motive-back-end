package org.motivepick.web

import org.motivepick.domain.entity.Task
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class Statistician {

    fun calculateStatisticsFor(tasks: List<Task>): Statistics {
        val openTasks = tasks.filterNot { task -> task.closed }
        val closedTasks = tasks.filter { task -> task.closed }

        return Statistics(openTasks.size, closedTasks.size, 0, 0, 0, LocalDateTime.now())
    }
}

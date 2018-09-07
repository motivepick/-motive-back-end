package org.motivepick.web

import org.motivepick.domain.entity.Task
import java.time.LocalDateTime

class GoalDto(val id: Long?, val name: String, val description: String?, val colorTag: String?, val created: LocalDateTime,
        val dueDate: LocalDateTime?, val closed: Boolean, val tasks: List<Task>)

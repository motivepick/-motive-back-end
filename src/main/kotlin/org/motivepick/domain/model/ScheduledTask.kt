package org.motivepick.domain.model

import org.motivepick.domain.entity.TaskEntity
import java.time.LocalDateTime

data class ScheduledTask(val id: Long, val name: String, val description: String, val dueDate: LocalDateTime, val closed: Boolean) {

    companion object {
        fun from(entity: TaskEntity): ScheduledTask = ScheduledTask(entity.id, entity.name, entity.description ?: "", entity.dueDate!!, entity.closed)
    }
}

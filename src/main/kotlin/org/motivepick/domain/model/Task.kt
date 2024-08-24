package org.motivepick.domain.model

import java.time.LocalDateTime

data class Task(val id: Long, val name: String, val description: String, val dueDate: LocalDateTime?, val closed: Boolean)

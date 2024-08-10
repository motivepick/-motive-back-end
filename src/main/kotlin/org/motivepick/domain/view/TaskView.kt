package org.motivepick.domain.view

import java.time.LocalDateTime

data class TaskView(val id: Long, val name: String, val description: String, val dueDate: LocalDateTime?, val closed: Boolean)

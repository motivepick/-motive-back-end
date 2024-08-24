package org.motivepick.domain.view

import java.time.OffsetDateTime

data class TaskView(val id: Long, val name: String, val description: String, val dueDate: OffsetDateTime?, val closed: Boolean)

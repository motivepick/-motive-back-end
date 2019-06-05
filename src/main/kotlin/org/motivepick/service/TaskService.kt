package org.motivepick.service

import org.motivepick.domain.entity.Task
import org.motivepick.domain.entity.User

interface TaskService {

    fun createInitialTasks(tasksOwner: User): Iterable<Task>
}

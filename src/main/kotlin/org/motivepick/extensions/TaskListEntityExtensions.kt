package org.motivepick.extensions

import org.motivepick.domain.entity.TaskListEntity
import org.motivepick.domain.model.TaskList

internal object TaskListEntityExtensions {

    fun TaskListEntity.model(): TaskList = TaskList(this.id, this.type)
}

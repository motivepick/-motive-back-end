package org.motivepick.extensions

import org.motivepick.domain.model.TaskList
import org.motivepick.domain.view.TaskListView

internal object TaskListExtensions {

    fun TaskList.view(): TaskListView = TaskListView(this.id, this.type)
}

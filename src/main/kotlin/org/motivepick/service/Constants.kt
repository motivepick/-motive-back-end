package org.motivepick.service

import org.motivepick.domain.entity.TaskListType.*

object Constants {

    /**
     * A task can only belong to one of these lists at a time.
     */
    val EXCLUSIVE_TASK_LISTS = listOf(INBOX, CLOSED, DELETED)

    val INCLUSIVE_TASK_LISTS = entries.toList().minus(EXCLUSIVE_TASK_LISTS)
}

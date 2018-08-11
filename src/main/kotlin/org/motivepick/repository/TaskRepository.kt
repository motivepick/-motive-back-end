package org.motivepick.repository

import org.motivepick.domain.entity.Task
import org.springframework.data.repository.PagingAndSortingRepository

interface TaskRepository : PagingAndSortingRepository<Task, Long> {

    fun findAllByUserAccountIdAndClosedOrderByCreatedDesc(userId: Long, closed: Boolean): List<Task>
}

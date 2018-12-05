package org.motivepick.repository

import org.motivepick.domain.entity.Task
import org.springframework.data.repository.PagingAndSortingRepository

interface TaskRepository : PagingAndSortingRepository<Task, Long> {

    fun findAllByUserAccountId(userId: Long): List<Task>

    fun findAllByUserAccountIdOrderByCreatedDesc(userId: Long): List<Task>

    fun findAllByUserAccountIdAndClosedFalseOrderByCreatedDesc(userId: Long): List<Task>

    fun findAllByUserAccountIdAndClosedTrueOrderByClosingDateDesc(userId: Long): List<Task>

    fun findAllByGoalIdAndClosedOrderByCreatedDesc(goalId: Long, closed: Boolean): List<Task>

    fun findAllByGoalId(goalId: Long): List<Task>
}

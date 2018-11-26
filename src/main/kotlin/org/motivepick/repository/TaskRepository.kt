package org.motivepick.repository

import org.motivepick.domain.entity.Task
import org.springframework.data.repository.PagingAndSortingRepository

interface TaskRepository : PagingAndSortingRepository<Task, Long> {

    fun findAllByUserAccountId(userId: Long): List<Task>

    fun findAllByUserAccountIdAndClosedFalseAndDueDateNotNull(userId: Long): List<Task>

    fun findAllByUserAccountIdAndClosedOrderByCreatedDesc(userId: Long, closed: Boolean): List<Task>

    fun findAllByGoalIdAndClosedOrderByCreatedDesc(goalId: Long, closed: Boolean): List<Task>

    fun findAllByGoalId(goalId: Long): List<Task>
}

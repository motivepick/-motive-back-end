package org.motivepick.repository

import org.motivepick.domain.entity.Task
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface TaskRepository : PagingAndSortingRepository<Task, Long> {

    fun findByIdAndVisibleTrue(id: Long): Optional<Task>

    fun findAllByUserAccountIdAndVisibleTrue(userId: Long): List<Task>

    fun findAllByUserAccountIdAndVisibleTrueOrderByCreatedDesc(userId: Long): List<Task>

    fun findAllByUserAccountIdAndClosedFalseAndVisibleTrueOrderByCreatedDesc(userId: Long): List<Task>

    fun findAllByUserAccountIdAndClosedTrueAndVisibleTrueOrderByClosingDateDesc(userId: Long): List<Task>

    fun findAllByGoalIdAndClosedOrderByCreatedDesc(goalId: Long, closed: Boolean): List<Task>

    fun findAllByGoalId(goalId: Long): List<Task>
}

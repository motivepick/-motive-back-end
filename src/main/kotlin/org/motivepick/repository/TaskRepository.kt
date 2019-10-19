package org.motivepick.repository

import org.motivepick.domain.entity.Task
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface TaskRepository : PagingAndSortingRepository<Task, Long> {

    fun findByIdAndVisibleTrue(id: Long): Optional<Task>

    fun findAllByUserAccountId(userId: String): List<Task>

    fun findAllByUserAccountIdAndClosedFalseAndDueDateNotNull(userId: String): List<Task>

    fun findAllByUserAccountIdAndVisibleTrueOrderByCreatedDesc(userId: String): List<Task>

    fun findAllByUserAccountIdAndClosedFalseAndVisibleTrueOrderByCreatedDesc(userId: String): List<Task>

    fun findAllByUserAccountIdAndClosedTrueAndVisibleTrueOrderByClosingDateDesc(userId: String): List<Task>
}

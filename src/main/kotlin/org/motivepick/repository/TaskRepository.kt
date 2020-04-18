package org.motivepick.repository

import org.motivepick.domain.entity.Task
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface TaskRepository : PagingAndSortingRepository<Task, Long> {

    fun findAllByUserAccountIdAndVisibleTrueOrderByCreatedDesc(userId: String): List<Task>

    fun findByIdAndVisibleTrue(id: Long): Optional<Task>

    fun findAllByUserAccountId(userId: String): List<Task>

    fun findAllByUserAccountIdAndClosedFalseAndDueDateNotNullAndVisibleTrue(userId: String): List<Task>

    fun findAllByIdInAndVisibleTrue(ids: List<Long>): List<Task>

    fun deleteByUserAccountId(userId: String)
}

package org.motivepick.repository

import org.motivepick.domain.entity.TaskEntity
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface TaskRepository : PagingAndSortingRepository<TaskEntity, Long> {

    fun findByIdAndVisibleTrue(id: Long): Optional<TaskEntity>

    fun findAllByUserAccountId(userId: String): List<TaskEntity>

    fun findAllByUserAccountIdAndClosedFalseAndDueDateNotNullAndVisibleTrue(userId: String): List<TaskEntity>

    fun findAllByIdInAndVisibleTrue(ids: List<Long>): List<TaskEntity>

    fun deleteByUserAccountId(userId: String)
}

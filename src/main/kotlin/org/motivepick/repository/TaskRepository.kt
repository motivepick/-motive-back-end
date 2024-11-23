package org.motivepick.repository

import org.motivepick.domain.entity.TaskEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface TaskRepository : CrudRepository<TaskEntity, Long>, PagingAndSortingRepository<TaskEntity, Long> {

    fun findByIdAndVisibleTrue(id: Long): Optional<TaskEntity>

    fun findAllByUserAccountId(userId: String): List<TaskEntity>

    fun findAllByUserAccountIdAndDueDateNotNullAndVisibleTrueOrderByDueDateAsc(userId: String): List<TaskEntity>

    fun findAllByIdInAndVisibleTrue(ids: List<Long>): List<TaskEntity>

    fun deleteByUserAccountId(userId: String)
}

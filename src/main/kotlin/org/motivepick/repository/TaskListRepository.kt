package org.motivepick.repository

import org.motivepick.domain.entity.TaskListEntity
import org.motivepick.domain.entity.TaskListType
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface TaskListRepository : CrudRepository<TaskListEntity, Long>, PagingAndSortingRepository<TaskListEntity, Long> {

    fun findAllByUserAccountId(accountId: String): List<TaskListEntity>

    fun findByUserAccountIdAndType(accountId: String, type: TaskListType): TaskListEntity?

    fun findByUserAccountIdAndId(accountId: String, id: Long): TaskListEntity?

    fun deleteByUserAccountId(accountId: String)

    fun deleteByIdIn(ids: List<Long>)
}

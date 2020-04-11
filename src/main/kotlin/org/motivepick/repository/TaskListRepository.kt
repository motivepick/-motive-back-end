package org.motivepick.repository

import org.motivepick.domain.entity.TaskListEntity
import org.motivepick.domain.entity.TaskListType
import org.springframework.data.repository.PagingAndSortingRepository

interface TaskListRepository : PagingAndSortingRepository<TaskListEntity, Long> {

    fun findByUserAccountIdAndType(accountId: String, type: TaskListType): TaskListEntity?

    fun deleteByUserAccountId(accountId: String)
}

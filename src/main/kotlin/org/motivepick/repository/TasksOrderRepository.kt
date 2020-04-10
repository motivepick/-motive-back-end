package org.motivepick.repository

import org.motivepick.domain.entity.TasksOrderEntity
import org.springframework.data.repository.PagingAndSortingRepository

interface TasksOrderRepository : PagingAndSortingRepository<TasksOrderEntity, Long> {

    fun findByUserAccountId(accountId: String): TasksOrderEntity?

    fun deleteByUserAccountId(accountId: String)
}

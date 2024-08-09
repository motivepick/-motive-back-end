package org.motivepick.repository

import org.motivepick.domain.entity.UserEntity
import org.springframework.data.repository.PagingAndSortingRepository

interface UserRepository : PagingAndSortingRepository<UserEntity, Long> {

    fun findByAccountId(accountId: String): UserEntity?

    fun deleteByAccountId(accountId: String)
}

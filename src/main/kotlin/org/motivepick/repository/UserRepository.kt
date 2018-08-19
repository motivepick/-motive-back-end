package org.motivepick.repository

import org.motivepick.domain.entity.User
import org.springframework.data.repository.PagingAndSortingRepository

interface UserRepository : PagingAndSortingRepository<User, Long> {

    fun existsByAccountId(accountId: Long): Boolean

    fun findByAccountId(accountId: Long): User?
}

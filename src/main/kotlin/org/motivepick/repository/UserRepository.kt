package org.motivepick.repository

import org.motivepick.domain.entity.User
import org.springframework.data.repository.PagingAndSortingRepository

interface UserRepository : PagingAndSortingRepository<User, Long> {

    fun findByAccountId(accountId: String): User?
}

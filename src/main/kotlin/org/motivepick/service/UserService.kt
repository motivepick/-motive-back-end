package org.motivepick.service

import org.motivepick.domain.entity.UserEntity
import org.motivepick.security.Profile

interface UserService {

    fun readCurrentUser(): UserEntity?

    fun createUserWithTasksIfNotExists(profile: Profile, language: String): UserEntity

    fun deleteTemporaryUserWithTasks(accountId: String)
}

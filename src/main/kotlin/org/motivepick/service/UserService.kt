package org.motivepick.service

import org.motivepick.domain.entity.User
import org.motivepick.security.Profile

interface UserService {

    fun readCurrentUser(): User?

    fun createUserWithTasksIfNotExists(profile: Profile): User
}

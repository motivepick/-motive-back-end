package org.motivepick.service

import org.motivepick.domain.view.UserView
import org.motivepick.security.Profile

interface UserService {

    fun readCurrentUser(): UserView?

    fun createUserWithTasksIfNotExists(profile: Profile, language: String): UserView

    fun deleteTemporaryUserWithTasks(accountId: String)
}

package org.motivepick.service

import org.motivepick.domain.view.UserView
import org.motivepick.security.Oauth2Profile

interface UserService {

    fun readCurrentUser(): UserView?

    fun createUserWithTasksIfNotExists(temporaryAccountId: String, oauth2Profile: Oauth2Profile, language: String): UserView

    fun deleteTemporaryUserWithTasks(accountId: String)
}

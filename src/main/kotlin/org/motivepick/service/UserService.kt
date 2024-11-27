package org.motivepick.service

import org.motivepick.domain.view.UserView
import org.motivepick.security.OAuth2Profile

interface UserService {

    fun readCurrentUser(): UserView?

    fun createUserWithTasksIfNotExists(temporaryAccountId: String, oAuth2Profile: OAuth2Profile, language: String): UserView

    fun deleteTemporaryUserWithTasks(accountId: String)
}

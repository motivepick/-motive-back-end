package org.motivepick.security

import org.motivepick.service.UserService

abstract class AbstractTokenGenerator<T>(private val userService: UserService, private val tokenService: JwtTokenService) {

    fun generateJwtToken(code: String, redirectUri: String, language: String): String {
        val accessToken = requestAccessToken(code, redirectUri)
        val profile = requestProfile(accessToken)
        userService.createUserWithTasksIfNotExists(profile, language)
        return tokenService.createAccessJwtToken(profile.id)
    }

    protected abstract fun requestAccessToken(code: String, redirectUri: String): T

    protected abstract fun requestProfile(response: T): Profile
}

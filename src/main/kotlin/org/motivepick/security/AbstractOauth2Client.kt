package org.motivepick.security

abstract class AbstractOauth2Client<T> {

    fun fetchUserProfile(code: String, redirectUri: String): Oauth2Profile {
        val accessToken = requestAccessToken(code, redirectUri)
        return requestProfile(accessToken)
    }

    protected abstract fun requestAccessToken(code: String, redirectUri: String): T

    protected abstract fun requestProfile(response: T): Oauth2Profile
}

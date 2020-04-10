package org.motivepick.security

import org.motivepick.config.Oauth2Config
import org.motivepick.service.UserService
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

abstract class AbstractTokenGenerator(private val userService: UserService, private val tokenService: JwtTokenService,
        private val config: Oauth2Config, private val httpClient: RestTemplate) {

    fun generateJwtToken(code: String, redirectUri: String): String {
        val accessToken = requestAccessToken(code, redirectUri)
        val profile = requestProfile(accessToken)
        userService.createUserWithTasksIfNotExists(profile)
        return tokenService.createAccessJwtToken(profile.id)
    }

    protected abstract fun requestProfile(accessToken: TokenResponse): Profile

    private fun requestAccessToken(code: String, redirectUri: String): TokenResponse {
        val uri = UriComponentsBuilder.fromUriString(config.accessTokenUri)
                .queryParam("client_id", config.clientId)
                .queryParam("client_secret", config.clientSecret)
                .queryParam("code", code)
                .queryParam("redirect_uri", redirectUri).build().toUri()
        return httpClient.getForObject(uri, TokenResponse::class.java)
                ?: throw AuthenticationServiceException("Could not retrieve access token")
    }
}

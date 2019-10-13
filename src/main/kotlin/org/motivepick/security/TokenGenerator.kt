package org.motivepick.security

import org.motivepick.config.Oauth2Config
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

abstract class TokenGenerator(private val config: Oauth2Config, private val httpClient: RestTemplate) {

    abstract fun generateJwtToken(code: String, redirectUri: String): String

    protected fun requestAccessToken(code: String, redirectUri: String): TokenResponse {
        val uri = UriComponentsBuilder.fromUriString(config.accessTokenUri)
                .queryParam("client_id", config.clientId)
                .queryParam("client_secret", config.clientSecret)
                .queryParam("code", code)
                .queryParam("redirect_uri", redirectUri).build().toUri()
        return httpClient.getForObject(uri, TokenResponse::class.java)
                ?: throw AuthenticationServiceException("Could not retrieve access token")
    }
}

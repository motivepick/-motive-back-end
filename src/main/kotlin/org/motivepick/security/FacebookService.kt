package org.motivepick.security

import com.fasterxml.jackson.annotation.JsonProperty
import org.motivepick.config.FacebookConfig
import org.motivepick.service.UserService
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class FacebookService(private val userService: UserService, private val jwtTokenFactory: JwtTokenFactory,
        private val config: FacebookConfig, private val httpClient: RestTemplate) : TokenGenerator(config, httpClient) {

    override fun generateJwtToken(code: String, redirectUri: String): String {
        val accessToken = requestAccessToken(code, redirectUri)
        val profile = requestProfile(accessToken)
        userService.createUserWithTasksIfNotExists(profile.id, profile.name, false)
        return jwtTokenFactory.createAccessJwtToken(profile.id)
    }

    private fun requestProfile(accessToken: TokenResponse): FacebookProfile {
        val uri = UriComponentsBuilder.fromUriString(config.userInfoUri)
                .queryParam("access_token", accessToken.token)
                .build().toUri()
        return httpClient.getForObject(uri, FacebookProfile::class.java)
                ?: throw AuthenticationServiceException("Could not retrieve profile")
    }

    internal data class FacebookProfile(

            @JsonProperty("id")
            val id: String,

            @JsonProperty("name")
            val name: String
    )
}

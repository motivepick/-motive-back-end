package org.motivepick.security

import com.fasterxml.jackson.annotation.JsonProperty
import org.motivepick.config.FacebookConfig
import org.motivepick.service.UserService
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class FacebookService(userService: UserService, tokenService: JwtTokenService, private val config: FacebookConfig,
        private val httpClient: RestTemplate) : AbstractTokenGenerator(userService, tokenService, config, httpClient) {

    override fun requestProfile(accessToken: TokenResponse): Profile =
        UriComponentsBuilder.fromUriString(config.userInfoUri)
            .queryParam("access_token", accessToken.token)
            .build()
            .toUri()
            .let { httpClient.getForObject(it, FacebookProfile::class.java) }
            ?.let { Profile(it.id, it.name, false) }
            ?: throw AuthenticationServiceException("Could not retrieve profile")

    internal data class FacebookProfile(

            @JsonProperty("id")
            val id: String,

            @JsonProperty("name")
            val name: String
    )
}

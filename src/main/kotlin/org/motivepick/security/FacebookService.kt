package org.motivepick.security

import com.fasterxml.jackson.annotation.JsonProperty
import org.motivepick.config.FacebookConfig
import org.motivepick.service.UserService
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class FacebookService(userService: UserService, jwtTokenFactory: JwtTokenFactory, private val config: FacebookConfig,
        private val httpClient: RestTemplate) : AbstractTokenGenerator(userService, jwtTokenFactory, config, httpClient) {

    override fun requestProfile(accessToken: TokenResponse): Profile {
        val uri = UriComponentsBuilder.fromUriString(config.userInfoUri)
                .queryParam("access_token", accessToken.token)
                .build().toUri()
        val response = httpClient.getForObject(uri, FacebookProfile::class.java)
                ?: throw AuthenticationServiceException("Could not retrieve profile")
        return Profile(response.id, response.name, false)
    }

    internal data class FacebookProfile(

            @JsonProperty("id")
            val id: String,

            @JsonProperty("name")
            val name: String
    )
}

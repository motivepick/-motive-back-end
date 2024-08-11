package org.motivepick.security

import com.fasterxml.jackson.annotation.JsonProperty
import org.motivepick.config.VkConfig
import org.motivepick.service.UserService
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class VkService(userService: UserService, jwtTokenService: JwtTokenService, private val config: VkConfig,
        private val httpClient: RestTemplate) : AbstractTokenGenerator(userService, jwtTokenService, config, httpClient) {

    override fun requestProfile(accessToken: TokenResponse): Profile =
        UriComponentsBuilder.fromUriString(config.userInfoUri)
            .queryParam("access_token", accessToken.token)
            .queryParam("user_ids", accessToken.id)
            .queryParam("v", config.apiVersion)
            .build()
            .toUri()
            .let { httpClient.getForObject(it, VkProfileResponse::class.java) }
            ?.let { it.profiles[0] }
            ?.let { Profile(accessToken.id!!, it.firstName + " " + it.lastName, false) }
            ?: throw AuthenticationServiceException("Could not retrieve profile")

    internal data class VkProfileResponse(

            @JsonProperty("response")
            val profiles: List<VkProfile>
    )

    internal data class VkProfile(

            @JsonProperty("first_name")
            val firstName: String,

            @JsonProperty("last_name")
            val lastName: String
    )
}

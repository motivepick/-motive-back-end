package org.motivepick.security

import com.fasterxml.jackson.annotation.JsonProperty
import org.motivepick.config.VkConfig
import org.motivepick.service.UserService
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class VkService(userService: UserService, jwtTokenFactory: JwtTokenFactory, private val config: VkConfig,
        private val httpClient: RestTemplate) : AbstractTokenGenerator(userService, jwtTokenFactory, config, httpClient) {

    override fun requestProfile(accessToken: TokenResponse): Profile {
        val uri = UriComponentsBuilder.fromUriString(config.userInfoUri)
                .queryParam("access_token", accessToken.token)
                .queryParam("user_ids", accessToken.id)
                .queryParam("v", config.apiVersion)
                .build().toUri()
        val response = httpClient.getForObject(uri, VkProfileResponse::class.java)
                ?: throw AuthenticationServiceException("Could not retrieve profile")
        val profile = response.profiles[0]
        return Profile(accessToken.id!!, profile.firstName + " " + profile.lastName, false)
    }

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

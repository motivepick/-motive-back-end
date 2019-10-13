package org.motivepick.security

import com.fasterxml.jackson.annotation.JsonProperty
import org.motivepick.config.VkConfig
import org.motivepick.service.UserService
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class VkService(private val userService: UserService, private val jwtTokenFactory: JwtTokenFactory,
        private val config: VkConfig, private val httpClient: RestTemplate) : TokenGenerator(config, httpClient) {

    override fun generateJwtToken(code: String, redirectUri: String): String {
        val accessToken = requestAccessToken(code, redirectUri)
        val profile = requestProfile(accessToken).response[0]
        userService.createUserWithTasksIfNotExists(accessToken.id!!, profile.firstName + " " + profile.lastName, false)
        return jwtTokenFactory.createAccessJwtToken(accessToken.id)
    }

    private fun requestProfile(accessToken: TokenResponse): VkProfileResponse {
        val uri = UriComponentsBuilder.fromUriString(config.userInfoUri)
                .queryParam("access_token", accessToken.token)
                .queryParam("user_ids", accessToken.id)
                .queryParam("v", config.apiVersion)
                .build().toUri()
        return httpClient.getForObject(uri, VkProfileResponse::class.java)
                ?: throw AuthenticationServiceException("Could not retrieve profile")
    }

    internal data class VkProfileResponse(

            @JsonProperty("response")
            val response: List<VkProfile>
    )

    internal data class VkProfile(

            @JsonProperty("first_name")
            val firstName: String,

            @JsonProperty("last_name")
            val lastName: String
    )
}

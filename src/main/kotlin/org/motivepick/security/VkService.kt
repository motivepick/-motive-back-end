package org.motivepick.security

import com.fasterxml.jackson.annotation.JsonProperty
import org.motivepick.config.VkConfig
import org.motivepick.domain.entity.User
import org.motivepick.repository.UserRepository
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class VkService(private val userRepo: UserRepository,
        private val jwtTokenFactory: JwtTokenFactory,
        private val vkConfig: VkConfig,
        private val restTemplate: RestTemplate) {

    fun generateJwtToken(code: String, redirectUri: String): String {
        val accessToken = requestAccessToken(code, redirectUri)
        val profile = requestProfile(accessToken)

        if (!userRepo.existsByAccountId(accessToken.id)) {
            val user = User(accessToken.id, profile.firstName + " " + profile.lastName)
            userRepo.save(user)
        }

        return jwtTokenFactory.createAccessJwtToken(accessToken.id.toString())
    }

    private fun requestAccessToken(code: String, redirectUri: String): TokenResponse {
        val uri = UriComponentsBuilder.fromUriString(vkConfig.accessTokenUri)
                .queryParam("client_id", vkConfig.clientId)
                .queryParam("client_secret", vkConfig.clientSecret)
                .queryParam("code", code)
                .queryParam("redirect_uri", redirectUri).build().toUri()
        return restTemplate.getForObject(uri, TokenResponse::class.java)
                ?: throw AuthenticationServiceException("Could not retrieve access token")
    }

    private fun requestProfile(accessToken: TokenResponse): VkProfile {
        val uri = UriComponentsBuilder.fromUriString(vkConfig.userInfoUri)
                .queryParam("access_token", accessToken.token)
                .queryParam("user_ids", accessToken.id)
                .queryParam("v", vkConfig.apiVersion)
                .build().toUri()
        val response = restTemplate.getForObject(uri, VkProfileResponse::class.java)
        if (response == null) {
            throw AuthenticationServiceException("Could not retrieve VK profile")
        } else {
            return response.response[0]
        }
    }

    internal data class TokenResponse(
            @JsonProperty("access_token")
            val token: String,

            @JsonProperty("user_id")
            val id: Long
    )

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
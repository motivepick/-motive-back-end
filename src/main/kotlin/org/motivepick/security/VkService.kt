package org.motivepick.security

import com.fasterxml.jackson.annotation.JsonProperty
import org.motivepick.config.VkConfig
import org.motivepick.domain.entity.User
import org.motivepick.repository.UserRepository
import org.motivepick.service.TaskService
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class VkService(private val userRepo: UserRepository,
        private val taskService: TaskService,
        private val jwtTokenFactory: JwtTokenFactory,
        private val config: VkConfig,
        private val restTemplate: RestTemplate) : TokenGenerator {

    override fun generateJwtToken(code: String, redirectUri: String): String {
        val accessToken = requestAccessToken(code, redirectUri)
        val profile = requestProfile(accessToken)

        if (!userRepo.existsByAccountId(accessToken.id)) {
            val user = userRepo.save(User(accessToken.id, profile.firstName + " " + profile.lastName))
            taskService.createInitialTasks(user)
        }

        return jwtTokenFactory.createAccessJwtToken(accessToken.id.toString())
    }

    private fun requestAccessToken(code: String, redirectUri: String): TokenResponse {
        val uri = UriComponentsBuilder.fromUriString(config.accessTokenUri)
                .queryParam("client_id", config.clientId)
                .queryParam("client_secret", config.clientSecret)
                .queryParam("code", code)
                .queryParam("redirect_uri", redirectUri).build().toUri()
        return restTemplate.getForObject(uri, TokenResponse::class.java)
                ?: throw AuthenticationServiceException("Could not retrieve access token")
    }

    private fun requestProfile(accessToken: TokenResponse): VkProfile {
        val uri = UriComponentsBuilder.fromUriString(config.userInfoUri)
                .queryParam("access_token", accessToken.token)
                .queryParam("user_ids", accessToken.id)
                .queryParam("v", config.apiVersion)
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

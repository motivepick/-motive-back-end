package org.motivepick.security

import com.fasterxml.jackson.annotation.JsonProperty
import org.motivepick.config.FacebookConfig
import org.motivepick.domain.entity.User
import org.motivepick.repository.UserRepository
import org.motivepick.service.TaskService
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class FacebookService(private val userRepo: UserRepository,
        private val taskService: TaskService,
        private val jwtTokenFactory: JwtTokenFactory,
        private val config: FacebookConfig,
        private val restTemplate: RestTemplate) : TokenGenerator {

    override fun generateJwtToken(code: String, redirectUri: String): String {
        val accessToken = requestAccessToken(code, redirectUri)
        val profile = requestProfile(accessToken)

        if (!userRepo.existsByAccountId(profile.id)) {
            val user = userRepo.save(User(profile.id, profile.name))
            taskService.createInitialTasks(user)
        }

        return jwtTokenFactory.createAccessJwtToken(profile.id.toString())
    }

    private fun requestAccessToken(code: String, redirectUri: String): String {
        val uri = UriComponentsBuilder.fromUriString(config.accessTokenUri)
                .queryParam("client_id", config.clientId)
                .queryParam("client_secret", config.clientSecret)
                .queryParam("code", code)
                .queryParam("redirect_uri", redirectUri).build().toUri()
        return restTemplate.getForObject(uri, TokenResponse::class.java)?.token
                ?: throw AuthenticationServiceException("Could not retrieve access token")
    }

    private fun requestProfile(accessToken: String): FacebookProfile {
        val uri = UriComponentsBuilder.fromUriString(config.userInfoUri)
                .queryParam("access_token", accessToken)
                .build().toUri()
        return restTemplate.getForObject(uri, FacebookProfile::class.java)
                ?: throw AuthenticationServiceException("Could not retrieve FB profile")
    }

    internal data class TokenResponse(
            @JsonProperty("access_token")
            val token: String
    )

    internal data class FacebookProfile(

            @JsonProperty("id")
            val id: Long,

            @JsonProperty("name")
            val name: String
    )
}
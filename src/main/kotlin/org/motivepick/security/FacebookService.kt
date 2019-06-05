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
        private val facebookConfig: FacebookConfig,
        private val restTemplate: RestTemplate) {

    fun generateJwtToken(code: String, redirectUri: String): String {
        val accessToken = requestAccessToken(code, redirectUri)
        val facebookProfile = requestFacebookProfile(accessToken)

        if (!userRepo.existsByAccountId(facebookProfile.id)) {
            val user = userRepo.save(User(facebookProfile.id, facebookProfile.name))
            taskService.createInitialTasks(user)
        }

        return jwtTokenFactory.createAccessJwtToken(facebookProfile.id.toString())
    }

    private fun requestAccessToken(code: String, redirectUri: String): String {
        val uri = UriComponentsBuilder.fromUriString(facebookConfig.accessTokenUri)
                .queryParam("client_id", facebookConfig.clientId)
                .queryParam("client_secret", facebookConfig.clientSecret)
                .queryParam("code", code)
                .queryParam("redirect_uri", redirectUri).build().toUri()
        return restTemplate.getForObject(uri, TokenResponse::class.java)?.token
                ?: throw AuthenticationServiceException("Could not retrieve access token")
    }

    private fun requestFacebookProfile(accessToken: String): FacebookProfile {
        val uri = UriComponentsBuilder.fromUriString(facebookConfig.userInfoUri)
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
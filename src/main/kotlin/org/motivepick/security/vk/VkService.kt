package org.motivepick.security.vk

import org.motivepick.config.VkConfig
import org.motivepick.security.AbstractTokenGenerator
import org.motivepick.security.JwtTokenService
import org.motivepick.security.Profile
import org.motivepick.service.UserService
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class VkService(
    userService: UserService, jwtTokenService: JwtTokenService, private val config: VkConfig,
    private val httpClient: RestTemplate
) : AbstractTokenGenerator<VkTokenResponse>(userService, jwtTokenService) {

    override fun requestAccessToken(code: String, redirectUri: String): VkTokenResponse {
        val uri = UriComponentsBuilder.fromUriString(config.accessTokenUri)
            .queryParam("client_id", config.clientId)
            .queryParam("client_secret", config.clientSecret)
            .queryParam("code", code)
            .queryParam("redirect_uri", redirectUri).build().toUri()
        return httpClient.getForObject(uri, VkTokenResponse::class.java)
            ?: throw AuthenticationServiceException("Could not retrieve access token")
    }

    override fun requestProfile(response: VkTokenResponse): Profile =
        UriComponentsBuilder.fromUriString(config.userInfoUri)
            .queryParam("access_token", response.token)
            .queryParam("user_ids", response.id)
            .queryParam("v", config.apiVersion)
            .build()
            .toUri()
            .let { httpClient.getForObject(it, VkProfileResponse::class.java) }
            ?.let { it.profiles[0] }
            ?.let { Profile(response.id!!, it.firstName + " " + it.lastName, false) }
            ?: throw AuthenticationServiceException("Could not retrieve profile")
}

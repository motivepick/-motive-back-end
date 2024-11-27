package org.motivepick.security.vk

import org.motivepick.config.VkConfig
import org.motivepick.security.AbstractOauth2Client
import org.motivepick.security.OAuth2Profile
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class VkClient(
    private val config: VkConfig,
    private val httpClient: RestTemplate
) : AbstractOauth2Client<VkTokenResponse>() {

    override fun requestAccessToken(code: String, redirectUri: String): VkTokenResponse {
        val uri = UriComponentsBuilder.fromUriString(config.accessTokenUri)
            .queryParam("client_id", config.clientId)
            .queryParam("client_secret", config.clientSecret)
            .queryParam("code", code)
            .queryParam("redirect_uri", redirectUri)
            .build()
            .toUri()
        return httpClient.getForObject(uri, VkTokenResponse::class.java)
            ?: throw AuthenticationServiceException("Could not retrieve access token")
    }

    override fun requestProfile(response: VkTokenResponse): OAuth2Profile =
        UriComponentsBuilder.fromUriString(config.userInfoUri)
            .queryParam("access_token", response.token)
            .queryParam("user_ids", response.id)
            .queryParam("v", config.apiVersion)
            .build()
            .toUri()
            .let { httpClient.getForObject(it, VkProfileResponse::class.java) }
            ?.let { it.profiles[0] }
            ?.let { OAuth2Profile(response.id!!, it.firstName + " " + it.lastName) }
            ?: throw AuthenticationServiceException("Could not retrieve profile")
}

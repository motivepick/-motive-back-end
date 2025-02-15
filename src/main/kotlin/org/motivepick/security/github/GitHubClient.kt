package org.motivepick.security.github

import org.motivepick.config.GitHubConfig
import org.motivepick.security.AbstractOauth2Client
import org.motivepick.security.OAuth2Profile
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Service
class GitHubClient(
    private val config: GitHubConfig,
    private val httpClient: RestTemplate
) : AbstractOauth2Client<GitHubTokenResponse>() {

    override fun requestAccessToken(code: String, redirectUri: String): GitHubTokenResponse {
        val uri = UriComponentsBuilder.fromUriString(config.accessTokenUri)
            .queryParam("client_id", config.clientId)
            .queryParam("client_secret", config.clientSecret)
            .queryParam("code", code)
            .queryParam("redirect_uri", redirectUri)
            .build()
            .toUri()
        return fetchAccessToken(uri) ?: throw AuthenticationServiceException("Could not retrieve access token")
    }

    override fun requestProfile(response: GitHubTokenResponse): OAuth2Profile =
        UriComponentsBuilder.fromUriString(config.userInfoUri)
            .build()
            .toUri()
            .let { fetchProfile(it, response.token) }
            ?.let { OAuth2Profile(it.id.toString(), it.login) }
            ?: throw AuthenticationServiceException("Could not retrieve profile")

    private fun fetchAccessToken(uri: URI): GitHubTokenResponse? {
        val headers = HttpHeaders()
        headers.set("Accept", "application/json")
        val response = httpClient.exchange(uri, HttpMethod.GET, HttpEntity<Void>(headers), GitHubTokenResponse::class.java)
        return response.body
    }

    private fun fetchProfile(uri: URI, token: String): GitHubProfile? {
        val headers = HttpHeaders()
        headers.set("Authorization", "Bearer $token")
        val response = httpClient.exchange(uri, HttpMethod.GET, HttpEntity<Void>(headers), GitHubProfile::class.java)
        return response.body
    }
}

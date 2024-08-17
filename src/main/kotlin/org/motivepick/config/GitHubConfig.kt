package org.motivepick.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class GitHubConfig(

    @Value("\${github.clientId}")
    override val clientId: String,

    @Value("\${github.clientSecret}")
    override val clientSecret: String,

    @Value("\${github.userAuthorizationUri}")
    override val userAuthorizationUri: String,

    @Value("\${github.accessTokenUri}")
    override val accessTokenUri: String,

    @Value("\${github.userInfoUri}")
    val userInfoUri: String
) : Oauth2Config

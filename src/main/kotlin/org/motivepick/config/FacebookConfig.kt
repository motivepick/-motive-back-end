package org.motivepick.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class FacebookConfig (

    @Value("\${facebook.clientId}")
    override val clientId: String,

    @Value("\${facebook.clientSecret}")
    val clientSecret: String,

    @Value("\${facebook.userAuthorizationUri}")
    override val userAuthorizationUri: String,

    @Value("\${facebook.accessTokenUri}")
    val accessTokenUri: String,

    @Value("\${facebook.userInfoUri}")
    val userInfoUri: String
) : Oauth2Config
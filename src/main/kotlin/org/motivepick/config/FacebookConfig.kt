package org.motivepick.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class FacebookConfig (

    @Value("\${facebook.clientId}")
    override val clientId: String,

    @Value("\${facebook.clientSecret}")
    override val clientSecret: String,

    @Value("\${facebook.userAuthorizationUri}")
    override val userAuthorizationUri: String,

    @Value("\${facebook.accessTokenUri}")
    override val accessTokenUri: String,

    @Value("\${facebook.userInfoUri}")
    val userInfoUri: String
) : Oauth2Config
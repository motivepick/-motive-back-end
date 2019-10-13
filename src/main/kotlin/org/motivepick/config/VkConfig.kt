package org.motivepick.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class VkConfig (

    @Value("\${vk.clientId}")
    override val clientId: String,

    @Value("\${vk.clientSecret}")
    override val clientSecret: String,

    @Value("\${vk.userAuthorizationUri}")
    override val userAuthorizationUri: String,

    @Value("\${vk.accessTokenUri}")
    override val accessTokenUri: String,

    @Value("\${vk.userInfoUri}")
    val userInfoUri: String,

    @Value("\${vk.apiVersion}")
    val apiVersion: String
): Oauth2Config
package org.motivepick.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class ServerConfig(

    @Value("\${enforce.https.for.oauth}")
    val enforceHttpsForOauth: Boolean,

    @Value("\${authentication.success.url.web}")
    val authenticationSuccessUrlWeb: String,

    @Value("\${authentication.success.url.mobile}")
    val authenticationSuccessUrlMobile: String,

    @Value("\${logout.success.url}")
    val logoutSuccessUrl: String,

    @Value("\${cookie.domain}")
    val cookieDomain: String,

    @Value("\${cookie.secure}")
    val cookieSecure: Boolean,

    @Value("\${cors.allowedOriginPattern}")
    val corsAllowedOriginPattern: String
)

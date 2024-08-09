package org.motivepick.config

import org.motivepick.security.JWT_TOKEN_COOKIE
import org.springframework.stereotype.Component
import jakarta.servlet.http.Cookie

@Component
class CookieFactory(private val config: ServerConfig) {

    fun cookie(jwtToken: String): Cookie = cookie(jwtToken, 3600 * 24 * 365)

    fun cookie(jwtToken: String, age: Int): Cookie {
        val cookie = Cookie(JWT_TOKEN_COOKIE, jwtToken)
        cookie.domain = config.cookieDomain
        cookie.path = "/"
        cookie.maxAge = age
        cookie.isHttpOnly = true
        return cookie
    }
}

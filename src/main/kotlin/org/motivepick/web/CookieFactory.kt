package org.motivepick.web

import org.motivepick.security.JWT_TOKEN_COOKIE
import org.springframework.stereotype.Component
import javax.servlet.http.Cookie

@Component
class CookieFactory(private val config: ServerConfig) {

    fun cookie(jwtToken: String): Cookie {
        val cookie = Cookie(JWT_TOKEN_COOKIE, jwtToken)
        cookie.domain = config.cookieDomain
        cookie.path = "/"
        cookie.maxAge = 3600 * 24 * 365
        cookie.isHttpOnly = true
        return cookie
    }

    fun cookie(jwtToken: String, age: Int): Cookie {
        val cookie = Cookie(JWT_TOKEN_COOKIE, jwtToken)
        cookie.domain = config.cookieDomain
        cookie.path = "/"
        cookie.maxAge = age
        cookie.isHttpOnly = true
        return cookie
    }
}

package org.motivepick.web

import org.motivepick.security.JWT_TOKEN_COOKIE
import org.springframework.stereotype.Component
import javax.servlet.http.Cookie

@Component
class CookieFactory {

    fun cookieToSet(jwtToken: String): Cookie {
        val cookie = Cookie(JWT_TOKEN_COOKIE, jwtToken)
        cookie.domain = "localhost"
        cookie.path = "/"
        cookie.maxAge = 3600 * 24 * 365
        cookie.isHttpOnly = false
        return cookie
    }
}

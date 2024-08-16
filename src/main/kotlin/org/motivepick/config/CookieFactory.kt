package org.motivepick.config

import jakarta.servlet.http.Cookie
import org.motivepick.security.AUTHENTICATION_SCHEME
import org.motivepick.security.JWT_TOKEN_COOKIE
import org.springframework.stereotype.Component
import java.net.URLEncoder

@Component
internal class CookieFactory(private val config: ServerConfig) {

    fun cookie(jwtToken: String): Cookie = cookie(jwtToken, 3600 * 24 * 365)

    fun cookie(jwtToken: String, age: Int): Cookie {
        val cookie = Cookie(JWT_TOKEN_COOKIE, URLEncoder.encode("$AUTHENTICATION_SCHEME $jwtToken", "UTF-8"))
        cookie.domain = config.cookieDomain
        cookie.path = "/"
        cookie.maxAge = age
        cookie.isHttpOnly = true
        cookie.secure = config.cookieSecure
        return cookie
    }
}

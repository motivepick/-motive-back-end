package org.motivepick.web

import org.springframework.stereotype.Component
import javax.servlet.http.Cookie

@Component
class CookieFactory {

    fun cookieToSet(jwtToken: String): Cookie = cookie(jwtToken, 3600 * 24 * 365)

    fun cookieToDelete(): Cookie {
        return cookie("deleted", 0)
    }

    private fun cookie(jwtToken: String?, maxAge: Int): Cookie {
        val cookie = Cookie("SESSION", jwtToken)
        cookie.domain = "localhost"
        cookie.path = "/"
        cookie.maxAge = maxAge
        cookie.isHttpOnly = true
        return cookie
    }
}

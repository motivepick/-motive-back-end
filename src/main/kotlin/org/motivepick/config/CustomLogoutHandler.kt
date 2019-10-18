package org.motivepick.config

import org.motivepick.security.JWT_TOKEN_COOKIE
import org.motivepick.web.CookieFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomLogoutHandler(private val cookieFactory: CookieFactory) : LogoutHandler {

    override fun logout(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        response!!.addCookie(cookieFactory.cookie(JWT_TOKEN_COOKIE, 0))
    }
}

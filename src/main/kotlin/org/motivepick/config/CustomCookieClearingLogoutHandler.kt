package org.motivepick.config

import org.motivepick.web.CookieFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomCookieClearingLogoutHandler(private val cookieFactory: CookieFactory) : LogoutHandler {

    override fun logout(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        val cookie = cookieFactory.cookieToSet("")
        response!!.addCookie(cookie)
    }
}

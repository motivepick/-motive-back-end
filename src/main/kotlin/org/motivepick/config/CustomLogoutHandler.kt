package org.motivepick.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import org.motivepick.security.JWT_TOKEN_COOKIE
import org.motivepick.security.JwtTokenService
import org.motivepick.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

/**
 * Spring-provided logout handler that suppose to delete cookie does not delete them, so using custom one.
 * Also, the custom logout handler deletes temporary user with tasks on logout.
 */
class CustomLogoutHandler(private val tokenService: JwtTokenService, private val userService: UserService,
        private val cookieFactory: CookieFactory) : LogoutHandler {

    private val logger: Logger = LoggerFactory.getLogger(CustomLogoutHandler::class.java)

    override fun logout(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        try {
            val token = tokenService.lookupToken(request!!)
            if (token == null) {
                logger.warn("Cannot delete temporary user since token is null, the user will not be deleted")
            } else {
                val claims: Jws<Claims> = tokenService.extractClaims(token)
                val accountId = claims.body.subject
                userService.deleteTemporaryUserWithTasks(accountId)
            }
        } finally {
            response!!.addCookie(cookieFactory.cookie(JWT_TOKEN_COOKIE, 0))
        }
    }
}

package org.motivepick.web

import org.motivepick.config.CookieFactory
import org.motivepick.config.ServerConfig
import org.motivepick.security.JwtTokenService
import org.motivepick.security.OAuth2Profile
import org.motivepick.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@RestController
internal class TemporaryAccountController(private val config: ServerConfig, private val tokenService: JwtTokenService,
        private val userService: UserService, private val cookieFactory: CookieFactory) {

    @GetMapping("/temporary/login")
    fun login(request: HttpServletRequest, response: HttpServletResponse) {
        val locale = request.locale
        val temporaryAccountId = UUID.randomUUID().toString()
        userService.createUserWithTasksIfNotExists(temporaryAccountId, OAuth2Profile.empty(), locale.language)
        val token = tokenService.createAccessJwtToken(temporaryAccountId)
        if (request.getParameter("mobile") == null) {
            response.addCookie(cookieFactory.cookie(token))
            response.sendRedirect(config.authenticationSuccessUrlWeb)
        } else {
            response.sendRedirect(config.authenticationSuccessUrlMobile + token)
        }
    }
}

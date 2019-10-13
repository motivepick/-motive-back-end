package org.motivepick.web

import org.motivepick.security.JwtTokenFactory
import org.motivepick.security.Profile
import org.motivepick.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/temporary/login")
class TemporaryAccountController(private val config: ServerConfig, private val tokenFactory: JwtTokenFactory,
        private val userService: UserService, private val cookieFactory: CookieFactory) {

    @GetMapping
    fun login(request: HttpServletRequest, response: HttpServletResponse) {
        val temporaryAccountId = UUID.randomUUID().toString()
        userService.createUserWithTasksIfNotExists(Profile(temporaryAccountId, "", true))
        val token = tokenFactory.createAccessJwtToken(temporaryAccountId)
        if (request.getParameter("mobile") == null) {
            response.addCookie(cookieFactory.cookieToSet(token))
            response.sendRedirect(config.authenticationSuccessUrlWeb)
        } else {
            response.sendRedirect(config.authenticationSuccessUrlMobile + token)
        }
    }
}

package org.motivepick.web

import org.motivepick.service.FacebookLoginService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@RestController
internal class FacebookController(private val loginService: FacebookLoginService) {

    @GetMapping("/oauth2/authorization/facebook")
    fun login(request: HttpServletRequest, response: HttpServletResponse) = loginService.login(request, response)

    @GetMapping("/oauth2/authorization/facebook/callback")
    fun loginCallback(request: HttpServletRequest, response: HttpServletResponse) =
        loginService.loginCallback(request, response)
}

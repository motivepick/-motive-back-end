package org.motivepick.web

import org.motivepick.service.GitHubLoginService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@RestController
internal class GitHubController(private val loginService: GitHubLoginService) {

    @GetMapping("/oauth2/authorization/github")
    fun login(request: HttpServletRequest, response: HttpServletResponse) = loginService.login(request, response)

    @GetMapping("/oauth2/authorization/github/callback")
    fun loginCallback(request: HttpServletRequest, response: HttpServletResponse) =
        loginService.loginCallback(request, response)
}

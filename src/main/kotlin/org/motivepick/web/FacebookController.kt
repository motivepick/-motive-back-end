package org.motivepick.web

import org.motivepick.config.FacebookConfig
import org.motivepick.security.FacebookService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/oauth2/authorization/facebook")
class FacebookController(private val oauth2LoginHandler: Oauth2LoginHandler) {

    @Autowired
    constructor(config: FacebookConfig, service: FacebookService, serverConfig: ServerConfig, cookieFactory: CookieFactory)
            : this(Oauth2LoginHandler(config, service, serverConfig, cookieFactory))

    @GetMapping
    fun login(request: HttpServletRequest, response: HttpServletResponse) = oauth2LoginHandler.login(request, response)

    @GetMapping("/callback")
    fun loginCallback(request: HttpServletRequest, response: HttpServletResponse) = oauth2LoginHandler.loginCallback(request, response)
}

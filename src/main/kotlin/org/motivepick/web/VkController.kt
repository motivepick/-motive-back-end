package org.motivepick.web

import org.motivepick.config.CookieFactory
import org.motivepick.config.ServerConfig
import org.motivepick.config.VkConfig
import org.motivepick.security.VkService
import org.motivepick.service.Oauth2LoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class VkController(private val oauth2LoginService: Oauth2LoginService) {

    @Autowired
    constructor(config: VkConfig, service: VkService, serverConfig: ServerConfig, cookieFactory: CookieFactory)
            : this(Oauth2LoginService(config, service, serverConfig, cookieFactory))

    @GetMapping("/oauth2/authorization/vk")
    fun login(request: HttpServletRequest, response: HttpServletResponse) = oauth2LoginService.login(request, response)

    @GetMapping("/oauth2/authorization/vk/callback")
    fun loginCallback(request: HttpServletRequest, response: HttpServletResponse) = oauth2LoginService.loginCallback(request, response)
}

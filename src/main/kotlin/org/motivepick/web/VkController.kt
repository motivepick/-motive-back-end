package org.motivepick.web

import org.motivepick.config.VkConfig
import org.motivepick.security.VkService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/oauth2/authorization/vk")
class VkController(private val oauth2LoginHandler: Oauth2LoginHandler) {

    @Autowired
    constructor(config: VkConfig, service: VkService, serverConfig: ServerConfig) : this(Oauth2LoginHandler(config, service, serverConfig))

    @GetMapping
    fun login(request: HttpServletRequest, response: HttpServletResponse) = oauth2LoginHandler.login(request, response)

    @GetMapping("/callback")
    fun loginCallback(request: HttpServletRequest, response: HttpServletResponse) = oauth2LoginHandler.loginCallback(request, response)
}

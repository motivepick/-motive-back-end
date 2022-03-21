package org.motivepick.web

import org.motivepick.service.VkLoginService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class VkController(private val vkLoginService: VkLoginService) {

    @GetMapping("/oauth2/authorization/vk")
    fun login(request: HttpServletRequest, response: HttpServletResponse) = vkLoginService.login(request, response)

    @GetMapping("/oauth2/authorization/vk/callback")
    fun loginCallback(request: HttpServletRequest, response: HttpServletResponse) =
        vkLoginService.loginCallback(request, response)
}

package org.motivepick.web

import com.github.benmanes.caffeine.cache.Caffeine
import org.motivepick.config.VkConfig
import org.motivepick.security.VkService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/oauth2/authorization/vk")
class VkController(private val vkConfig: VkConfig,

        private val vkService: VkService,

        @Value("\${enforce.https.for.oauth}")
        private val enforceHttpsForOauth: Boolean,

        @Value("\${authentication.success.url.web}")
        private val authenticationSuccessUrlWeb: String,

        @Value("\${authentication.success.url.mobile}")
        private val authenticationSuccessUrlMobile: String) {

    private val validState = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build<String, Boolean>()

    @GetMapping
    fun login(request: HttpServletRequest, response: HttpServletResponse) {
        val uuid = UUID.randomUUID().toString()
        val state = Base64.getEncoder().encodeToString(uuid.toByteArray())
        val mobile = request.getParameter("mobile") != null
        validState.put(uuid, mobile)

        val redirectUrl = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .scheme(if (enforceHttpsForOauth) "https" else "http")
                .path("/callback")
                .toUriString()

        val authorizationUri = UriComponentsBuilder.fromUriString(vkConfig.userAuthorizationUri)
                .queryParam("client_id", vkConfig.clientId)
                .queryParam("redirect_uri", redirectUrl)
                .queryParam("state", state)
                .queryParam("v", vkConfig.apiVersion)
                .toUriString()

        response.sendRedirect(authorizationUri)
    }

    @GetMapping("/callback")
    fun loginCallback(request: HttpServletRequest, response: HttpServletResponse) {
        val code = request.getParameter("code")
        val state = String(Base64.getDecoder().decode(request.getParameter("state")))
        val mobile = validState.getIfPresent(state)
                ?: throw AuthenticationServiceException("Provided state is incorrect or expired")

        val redirectUrl = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .scheme(if (enforceHttpsForOauth) "https" else "http")
                .toUriString()
        val jwtToken = vkService.generateJwtToken(code, redirectUrl)

        val navigationUrl = if (mobile) authenticationSuccessUrlMobile else authenticationSuccessUrlWeb

        response.sendRedirect(navigationUrl + jwtToken)
    }
}
package org.motivepick.web

import com.github.benmanes.caffeine.cache.Caffeine
import org.motivepick.config.FacebookConfig
import org.motivepick.security.FacebookService
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
@RequestMapping("/oauth2/authorization/facebook")
class FacebookController(private val facebookConfig: FacebookConfig,

                         private val facebookService: FacebookService,

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

        val authorizationUri = UriComponentsBuilder.fromUriString(facebookConfig.userAuthorizationUri)
                .queryParam("client_id", facebookConfig.clientId)
                .queryParam("redirect_uri", ServletUriComponentsBuilder.fromCurrentRequestUri().scheme("https").path("/callback").toUriString())
                .queryParam("state", state)
                .toUriString()

        response.sendRedirect(authorizationUri)
    }

    @GetMapping("/callback")
    fun loginCallback(request: HttpServletRequest, response: HttpServletResponse) {
        val code = request.getParameter("code")
        val state = String(Base64.getDecoder().decode(request.getParameter("state")))
        val mobile = validState.getIfPresent(state) ?: throw AuthenticationServiceException("Provided state is incorrect or expired")

        val redirectUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString()
        val jwtToken = facebookService.generateJwtToken(code, redirectUrl)

        val navigationUrl = if (mobile) authenticationSuccessUrlMobile else authenticationSuccessUrlWeb

        response.sendRedirect(navigationUrl + jwtToken)
    }
}
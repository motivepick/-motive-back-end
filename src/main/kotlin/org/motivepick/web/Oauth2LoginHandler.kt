package org.motivepick.web

import com.github.benmanes.caffeine.cache.Caffeine
import org.motivepick.config.Oauth2Config
import org.motivepick.security.TokenGenerator
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.util.*
import java.util.concurrent.TimeUnit.MINUTES
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Oauth2LoginHandler(private val config: Oauth2Config, private val tokenGenerator: TokenGenerator, private val serverConfig: ServerConfig) {

    private val validState = Caffeine.newBuilder().expireAfterWrite(1, MINUTES).build<String, Boolean>()

    fun login(request: HttpServletRequest, response: HttpServletResponse) {
        val uuid = UUID.randomUUID().toString()
        val state = Base64.getEncoder().encodeToString(uuid.toByteArray())
        val mobile = request.getParameter("mobile") != null
        validState.put(uuid, mobile)

        val redirectUrl = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .scheme(if (serverConfig.enforceHttpsForOauth) "https" else "http")
                .path("/callback")
                .toUriString()

        val authorizationUri = UriComponentsBuilder.fromUriString(config.userAuthorizationUri)
                .queryParam("client_id", config.clientId)
                .queryParam("redirect_uri", redirectUrl)
                .queryParam("state", state)
                .toUriString()

        response.sendRedirect(authorizationUri)
    }

    fun loginCallback(request: HttpServletRequest, response: HttpServletResponse) {
        val code = request.getParameter("code")
        val state = String(Base64.getDecoder().decode(request.getParameter("state")))
        val mobile = validState.getIfPresent(state)
                ?: throw AuthenticationServiceException("Provided state is incorrect or expired")

        val redirectUrl = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .scheme(if (serverConfig.enforceHttpsForOauth) "https" else "http")
                .toUriString()
        val jwtToken = tokenGenerator.generateJwtToken(code, redirectUrl)

        val navigationUrl = if (mobile) serverConfig.authenticationSuccessUrlMobile else serverConfig.authenticationSuccessUrlWeb

        response.sendRedirect(navigationUrl + jwtToken)
    }
}

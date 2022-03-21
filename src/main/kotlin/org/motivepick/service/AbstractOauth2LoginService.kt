package org.motivepick.service

import org.motivepick.config.CookieFactory
import org.motivepick.config.Oauth2Config
import org.motivepick.config.ServerConfig
import org.motivepick.domain.entity.LoginStateEntity
import org.motivepick.repository.LoginStateRepository
import org.motivepick.security.AbstractTokenGenerator
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponentsBuilder
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class AbstractOauth2LoginService(
    private val config: Oauth2Config,
    private val tokenGenerator: AbstractTokenGenerator,
    private val serverConfig: ServerConfig,
    private val cookieFactory: CookieFactory,
    private val loginStateRepository: LoginStateRepository
) : Oauth2LoginService {

    @Transactional
    override fun login(request: HttpServletRequest, response: HttpServletResponse) {
        val uuid = UUID.randomUUID().toString()
        val state = Base64.getEncoder().encodeToString(uuid.toByteArray())
        val mobile = request.getParameter("mobile") != null
        loginStateRepository.save(LoginStateEntity(uuid, mobile))

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

    @Transactional
    override fun loginCallback(request: HttpServletRequest, response: HttpServletResponse) {
        val locale = request.locale
        val code = request.getParameter("code")
        val stateUuid = String(Base64.getDecoder().decode(request.getParameter("state")))
        val state = loginStateRepository.findByStateUuid(stateUuid)
            .orElseThrow { AuthenticationServiceException("Provided state is incorrect or expired") }

        val redirectUrl = ServletUriComponentsBuilder.fromCurrentRequestUri()
            .scheme(if (serverConfig.enforceHttpsForOauth) "https" else "http")
            .toUriString()
        val jwtToken = tokenGenerator.generateJwtToken(code, redirectUrl, locale.language)

        if (state.mobile) {
            response.sendRedirect(serverConfig.authenticationSuccessUrlMobile + jwtToken)
        } else {
            response.addCookie(cookieFactory.cookie(jwtToken))
            response.sendRedirect(serverConfig.authenticationSuccessUrlWeb)
        }

        loginStateRepository.deleteByStateUuid(stateUuid)
    }
}

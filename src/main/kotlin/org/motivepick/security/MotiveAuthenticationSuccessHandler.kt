package org.motivepick.security

import org.motivepick.domain.entity.User
import org.motivepick.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class MotiveAuthenticationSuccessHandler(@Value("\${authentication.success.url.web}")
                                         private val authenticationSuccessUrlWeb: String,

                                         @Value("\${authentication.success.url.mobile}")
                                         private val authenticationSuccessUrlMobile: String,

                                         private val userRepo: UserRepository)
    : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        if (authentication is OAuth2Authentication) {
            val accountId = getAccountId(authentication)
            if (!userRepo.existsByAccountId(accountId)) {
                // TODO: do we need to store access tokens?
                val user = User(accountId, getUserName(authentication))
                userRepo.save(user)
            }
        }

        val navigationUrl = request.getParameter("mobile")?.let {
            val cookie = request.cookies.find { cookie -> cookie.name == "SESSION" }!!.value

            return@let authenticationSuccessUrlMobile + cookie
        } ?: authenticationSuccessUrlWeb

        response.sendRedirect(navigationUrl)

        clearAuthenticationAttributes(request)
    }

    private fun clearAuthenticationAttributes(request: HttpServletRequest) {
        val session = request.getSession(false) ?: return

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)
    }

    private fun getAccountId(authentication: OAuth2Authentication): Long {
        val details = authentication.userAuthentication.details as Map<*, *>
        return details["id"]!!.toString().toLong()
    }

    private fun getUserName(authentication: OAuth2Authentication): String {
        val details = authentication.userAuthentication.details as Map<*, *>
        return details["name"]!!.toString()
    }
}
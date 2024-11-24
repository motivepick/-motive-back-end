package org.motivepick.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher

const val JWT_TOKEN_COOKIE = "Authorization"
const val AUTHENTICATION_SCHEME = "Bearer"

class JwtTokenAuthenticationProcessingFilter(matcher: RequestMatcher,
        private val tokenService: JwtTokenService) : AbstractAuthenticationProcessingFilter(matcher) {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val token = tokenService.lookupToken(request)
        return if (token.isBlank()) {
            throw BadCredentialsException("There is no JWT token in the request")
        } else {
            val claims: Jws<Claims>
            try {
                claims = tokenService.extractClaims(token)
            } catch (e: MalformedJwtException) {
                throw AuthenticationServiceException("Incorrect JWT token")
            }
            val subject = claims.body.subject
            val scopes = claims.body.get("scopes", List::class.java)
            val authorities = scopes.map { SimpleGrantedAuthority(it as String) }
            val user = User(subject, "", authorities)
            UsernamePasswordAuthenticationToken(user, null, authorities)
        }
    }

    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain,
            authResult: Authentication) {
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authResult
        SecurityContextHolder.setContext(context)
        chain.doFilter(request, response)
    }
}

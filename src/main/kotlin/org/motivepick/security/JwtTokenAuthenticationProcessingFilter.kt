package org.motivepick.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.MalformedJwtException
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private const val JWT_TOKEN_COOKIE = "SESSION"

class JwtTokenAuthenticationProcessingFilter(matcher: RequestMatcher,
                                             private val jwtTokenFactory: JwtTokenFactory)
    : AbstractAuthenticationProcessingFilter(matcher) {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val token = lookupToken(request)

        val claims: Jws<Claims>
        try {
            claims = jwtTokenFactory.extractClaims(token)
        } catch (e: MalformedJwtException) {
            throw AuthenticationServiceException("Incorrect JWT token")
        }
        val subject = claims.body.subject
        val scopes = claims.body.get("scopes", List::class.java)
        val authorities = scopes.map { SimpleGrantedAuthority(it as String) }

        return UsernamePasswordAuthenticationToken(subject, null, authorities)
    }

    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain,
                                          authResult: Authentication) {
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authResult
        SecurityContextHolder.setContext(context)
        chain.doFilter(request, response)
    }

    private fun lookupToken(request: HttpServletRequest): String {
        return request.cookies?.find { it.name == JWT_TOKEN_COOKIE }?.value
                ?: throw AuthenticationServiceException("Could not find authorization header")
    }
}

package org.motivepick.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Component
class JwtTokenService(
    @Value("\${jwt.token.issuer}")
    private val tokenIssuer: String,

    @Value("\${jwt.token.signing.key}")
    private val tokenSigningKey: String
) {

    fun createAccessJwtToken(accountId: String): String {
        val claims = Jwts.claims().setSubject(accountId)
        claims["scopes"] = arrayOf("ROLE_USER")

        return Jwts.builder()
            .setClaims(claims)
            .setIssuer(tokenIssuer)
            .setIssuedAt(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
            .signWith(SignatureAlgorithm.HS512, tokenSigningKey)
            .compact()
    }

    fun extractClaims(token: String): Jws<Claims> = Jwts.parser().setSigningKey(tokenSigningKey).parseClaimsJws(token)

    fun lookupToken(request: HttpServletRequest): String = request.cookies?.find { it.name == JWT_TOKEN_COOKIE }?.value ?: ""
}

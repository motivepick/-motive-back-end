package org.motivepick.security

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;

import static org.motivepick.security.Constants.JWT_TOKEN_COOKIE;

@Component
public class JwtTokenService {

    private final String tokenIssuer;

    private final String tokenSigningKey;

    public JwtTokenService(@Value("${jwt.token.issuer}") String tokenIssuer, @Value("${jwt.token.signing.key}") String tokenSigningKey) {
        this.tokenIssuer = tokenIssuer;
        this.tokenSigningKey = tokenSigningKey;
    }

    String createAccessJwtToken(String accountId) {
        var claims = Jwts.claims().setSubject(accountId);
        claims.put("scopes", new String[]{"ROLE_USER"});

        return Jwts.builder()
            .setClaims(claims)
            .setIssuer(tokenIssuer)
            .setIssuedAt(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
            .signWith(SignatureAlgorithm.HS512, tokenSigningKey)
            .compact();
    }

    public Jws<Claims> extractClaims(String token) {
        return Jwts.parser().setSigningKey(tokenSigningKey).parseClaimsJws(token);
    }

    /**
     * Note that it does not throw [AuthenticationServiceException] anymore because for callbacks
     * the JWT token may be absent (in a normal situation, i.e., if an unknown user tries to authenticate) or
     * may be present (if a temporary user tries to promote himself to a permanent one).
     */
    public String lookupToken(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
            .filter(it -> JWT_TOKEN_COOKIE.equals(it.getName()))
            .map(Cookie::getValue).findFirst()
            .orElse(null); // TODO: consider empty string
    }
}

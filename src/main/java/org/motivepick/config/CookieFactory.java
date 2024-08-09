package org.motivepick.config

import org.motivepick.security.Constants;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

@Component
class CookieFactory {

    private final ServerConfig config;

    public CookieFactory(ServerConfig config) {
        this.config = config;
    }

    Cookie cookie(String jwtToken) {
        return cookie(jwtToken, 3600 * 24 * 365);
    }

    Cookie cookie(String jwtToken, int age) {
        var cookie = new Cookie(Constants.JWT_TOKEN_COOKIE, jwtToken);
        cookie.setDomain(config.cookieDomain);
        cookie.setPath("/");
        cookie.setMaxAge(age);
        cookie.setHttpOnly(true);
        return cookie;
    }
}

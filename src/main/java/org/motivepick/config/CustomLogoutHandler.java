package org.motivepick.config

import org.motivepick.security.JwtTokenService;
import org.motivepick.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.motivepick.security.Constants.JWT_TOKEN_COOKIE;

/**
 * Spring-provided logout handler that suppose to delete cookie does not delete them, so using custom one.
 * Also, the custom logout handler deletes temporary user with tasks on logout.
 */
class CustomLogoutHandler implements LogoutHandler {

    private final JwtTokenService tokenService;
    private final UserService userService;
    private final CookieFactory cookieFactory;
    private final Logger logger;

    CustomLogoutHandler(JwtTokenService tokenService, UserService userService, CookieFactory cookieFactory) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.cookieFactory = cookieFactory;
        this.logger = LoggerFactory.getLogger(CustomLogoutHandler.class);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            var token = tokenService.lookupToken(request);
            if (token == null) {
                logger.warn("Cannot delete temporary user since token is null, the user will not be deleted");
            } else {
                var claims = tokenService.extractClaims(token);
                var accountId = claims.getBody().getSubject();
                userService.deleteTemporaryUserWithTasks(accountId);
            }
        } finally {
            response.addCookie(cookieFactory.cookie(JWT_TOKEN_COOKIE, 0));
        }
    }
}

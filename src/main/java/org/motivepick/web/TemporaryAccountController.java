package org.motivepick.web;

import org.jetbrains.annotations.NotNull;
import org.motivepick.config.CookieFactory;
import org.motivepick.config.ServerConfig;
import org.motivepick.security.JwtTokenService;
import org.motivepick.security.Profile;
import org.motivepick.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@RestController
public class TemporaryAccountController {

    private final ServerConfig config;
    private final JwtTokenService tokenService;
    private final UserService userService;
    private final CookieFactory cookieFactory;

    public TemporaryAccountController(ServerConfig config, JwtTokenService tokenService, UserService userService, CookieFactory cookieFactory) {
        this.config = config;
        this.tokenService = tokenService;
        this.userService = userService;
        this.cookieFactory = cookieFactory;
    }

    @GetMapping("/temporary/login")
    void login(HttpServletRequest request, HttpServletResponse response) {
        var locale = request.getLocale();
        var temporaryAccountId = UUID.randomUUID().toString();
        userService.createUserWithTasksIfNotExists(new Profile(temporaryAccountId, "", true), locale.getLanguage());
        var token = tokenService.createAccessJwtToken(temporaryAccountId);
        if (request.getParameter("mobile") == null) {
            response.addCookie(cookieFactory.cookie(token));
            sendRedirect(response, config.getAuthenticationSuccessUrlWeb());
        } else {
            sendRedirect(response, config.getAuthenticationSuccessUrlMobile() + token);
        }
    }

    private static void sendRedirect(@NotNull HttpServletResponse response, String authorizationUri) {
        try {
            response.sendRedirect(authorizationUri);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception happened when trying to send redirect", e);
        }
    }
}

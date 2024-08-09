package org.motivepick.service;

import com.google.common.base.Charsets;
import org.jetbrains.annotations.NotNull;
import org.motivepick.config.CookieFactory;
import org.motivepick.config.Oauth2Config;
import org.motivepick.config.ServerConfig;
import org.motivepick.domain.entity.LoginStateEntity;
import org.motivepick.repository.LoginStateRepository;
import org.motivepick.security.AbstractTokenGenerator;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

abstract class AbstractOauth2LoginService implements Oauth2LoginService {

    private final Oauth2Config config;
    private final AbstractTokenGenerator tokenGenerator;
    private final ServerConfig serverConfig;
    private final CookieFactory cookieFactory;
    private final LoginStateRepository loginStateRepository;

    public AbstractOauth2LoginService(Oauth2Config config, AbstractTokenGenerator tokenGenerator, ServerConfig serverConfig,
        CookieFactory cookieFactory, LoginStateRepository loginStateRepository) {
        this.config = config;
        this.tokenGenerator = tokenGenerator;
        this.serverConfig = serverConfig;
        this.cookieFactory = cookieFactory;
        this.loginStateRepository = loginStateRepository;
    }

    @Override
    public void login(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        var uuid = UUID.randomUUID().toString();
        var state = Base64.getEncoder().encodeToString(uuid.getBytes(Charsets.UTF_8));
        boolean mobile = request.getParameter("mobile") != null;
        loginStateRepository.save(new LoginStateEntity(uuid, mobile));

        var redirectUrl = ServletUriComponentsBuilder.fromCurrentRequestUri()
            .scheme(serverConfig.getEnforceHttpsForOauth() ? "https" : "http")
            .path("/callback")
            .toUriString();

        var authorizationUri = UriComponentsBuilder.fromUriString(config.getUserAuthorizationUri())
            .queryParam("client_id", config.getClientId())
            .queryParam("redirect_uri", redirectUrl)
            .queryParam("state", state)
            .toUriString();

        sendRedirect(response, authorizationUri);
    }

    @Override
    public void loginCallback(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        var locale = request.getLocale();
        var code = request.getParameter("code");
        var stateUuid = new String(Base64.getDecoder().decode(request.getParameter("state")));
        var state = loginStateRepository.findByStateUuid(stateUuid)
            .orElseThrow(() -> new AuthenticationServiceException("Provided state is incorrect or expired"));

        var redirectUrl = ServletUriComponentsBuilder.fromCurrentRequestUri()
            .scheme(serverConfig.getEnforceHttpsForOauth() ? "https" : "http")
            .toUriString();
        var jwtToken = tokenGenerator.generateJwtToken(code, redirectUrl, locale.getLanguage());

        if (state.getMobile()) {
            sendRedirect(response, serverConfig.getAuthenticationSuccessUrlMobile() + jwtToken);
        } else {
            response.addCookie(cookieFactory.cookie(jwtToken));
            sendRedirect(response, serverConfig.getAuthenticationSuccessUrlWeb());
        }

        loginStateRepository.deleteByStateUuid(stateUuid);
    }

    private static void sendRedirect(@NotNull HttpServletResponse response, String authorizationUri) {
        try {
            response.sendRedirect(authorizationUri);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception happened when trying to send redirect", e);
        }
    }
}

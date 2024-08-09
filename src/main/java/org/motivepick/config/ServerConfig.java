package org.motivepick.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

// TODO: check if works with Spring
@Configuration
class ServerConfig {
    @Value("${enforce.https.for.oauth}")
    boolean enforceHttpsForOauth;

    @Value("${authentication.success.url.web}")
    String authenticationSuccessUrlWeb;

    @Value("${authentication.success.url.mobile}")
    String authenticationSuccessUrlMobile;

    @Value("${logout.success.url}")
    String logoutSuccessUrl;

    @Value("${cookie.domain}")
    String cookieDomain;
}

package org.motivepick.config

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
class FacebookConfig implements Oauth2Config {

    private String clientId;
    private String clientSecret;
    private String userAuthorizationUri;
    private String accessTokenUri;
    private String userInfoUri;


    @Override
    @Value("${facebook.clientId}")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    @Value("${facebook.clientSecret}")
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    @Value("${facebook.userAuthorizationUri}")
    public String getUserAuthorizationUri() {
        return userAuthorizationUri;
    }

    public void setUserAuthorizationUri(String userAuthorizationUri) {
        this.userAuthorizationUri = userAuthorizationUri;
    }

    @Override
    @Value("${facebook.accessTokenUri}")
    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    public void setAccessTokenUri(String accessTokenUri) {
        this.accessTokenUri = accessTokenUri;
    }

    @Override
    @Value("${facebook.userInfoUri}")
    public String getUserInfoUri() {
        return userInfoUri;
    }

    public void setUserInfoUri(String userInfoUri) {
        this.userInfoUri = userInfoUri;
    }
}
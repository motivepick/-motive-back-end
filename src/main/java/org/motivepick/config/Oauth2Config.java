package org.motivepick.config

interface Oauth2Config {

    String getClientId();

    String getClientSecret();

    String getUserAuthorizationUri();

    String getAccessTokenUri();

    String getUserInfoUri();
}

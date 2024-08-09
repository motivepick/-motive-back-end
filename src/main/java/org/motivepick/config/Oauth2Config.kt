package org.motivepick.config

interface Oauth2Config {

    val clientId: String

    val clientSecret: String

    val userAuthorizationUri: String

    val accessTokenUri: String
}

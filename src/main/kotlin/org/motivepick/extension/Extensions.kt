package org.motivepick.extension

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

fun OAuth2AuthenticationToken.getAccountId(): Long = this.principal.attributes["id"]?.toString()?.toLong()
        ?: throw IllegalArgumentException("Could not lookup OAuth2 accountId")

fun OAuth2AuthenticationToken.getUsername(): String = this.principal.attributes["name"]?.toString()
        ?: throw IllegalArgumentException("Could not lookup OAuth2 username")
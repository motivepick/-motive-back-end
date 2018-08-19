package org.motivepick.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Component

@Component
class CurrentUser {

    fun getAccountId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication is OAuth2AuthenticationToken) {
            return authentication.principal.attributes["id"]!!.toString().toLong()
        }
        throw IllegalArgumentException("Could not lookup accountId")
    }
}
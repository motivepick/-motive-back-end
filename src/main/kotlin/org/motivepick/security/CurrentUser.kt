package org.motivepick.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Component

@Component
class CurrentUser {

    fun getAccountId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication is OAuth2Authentication) {
            return getAccountIdFromAuthentication(authentication)
        }
        throw UsernameNotFoundException("Could not lookup accountId")
    }

    private fun getAccountIdFromAuthentication(authentication: OAuth2Authentication): Long {
        val details = authentication.userAuthentication.details as Map<*, *>
        return details["id"]!!.toString().toLong()
    }
}

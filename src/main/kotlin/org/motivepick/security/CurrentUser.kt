package org.motivepick.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Component
class CurrentUser {

    fun getAccountId(): Long {
        val accountIdFromRequestHeader = accountIdFromRequestHeader()
        if (accountIdFromRequestHeader.isNullOrBlank()) {
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication is OAuth2Authentication) {
                return getAccountIdFromAuthentication(authentication)
            }
            throw UsernameNotFoundException("Could not lookup accountId")
        } else {
            return accountIdFromRequestHeader!!.toLong()
        }
    }

    /**
     * Temporary solution to not block development of the mobile part. Going to be deleted as soon as
     * OAuth2 for the mobile app will be fixed.
     */
    private fun accountIdFromRequestHeader(): String? {
        val attributes = RequestContextHolder.getRequestAttributes() ?: return ""
        return (attributes as ServletRequestAttributes).request.getHeader("X-Account-Id")
    }

    private fun getAccountIdFromAuthentication(authentication: OAuth2Authentication): Long {
        val details = authentication.userAuthentication.details as Map<*, *>
        return details["id"]!!.toString().toLong()
    }
}

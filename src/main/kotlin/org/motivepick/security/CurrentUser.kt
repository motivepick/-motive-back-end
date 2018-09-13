package org.motivepick.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class CurrentUser {

    fun getAccountId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication is UsernamePasswordAuthenticationToken) {
            return authentication.principal.toString().toLong()
        }
        throw UsernameNotFoundException("Could not lookup accountId")
    }
}

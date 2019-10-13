package org.motivepick.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class CurrentUser {

    fun getAccountId(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication is UsernamePasswordAuthenticationToken) {
            val principal = authentication.principal;
            if (principal == null) {
                throw UsernameNotFoundException("authentication is present, but user account is absent")
            } else {
                return principal.toString()
            }
        }
        throw UsernameNotFoundException("authentication is absent or has unexpected type")
    }
}

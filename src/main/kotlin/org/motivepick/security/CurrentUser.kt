package org.motivepick.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class CurrentUser {

    fun getAccountId(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication is UsernamePasswordAuthenticationToken) {
            when (val principal = authentication.principal) {
                null -> throw UsernameNotFoundException("Authentication is present, but user account is absent")
                is User -> return principal.username
                else -> throw UsernameNotFoundException("Authentication and principal are present, but principal has unexpected type ${principal.javaClass.name}")
            }
        }
        throw UsernameNotFoundException("Authentication is absent or has unexpected type")
    }
}

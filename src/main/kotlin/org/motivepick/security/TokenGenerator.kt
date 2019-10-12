package org.motivepick.security

interface TokenGenerator {

    fun generateJwtToken(code: String, redirectUri: String): String
}

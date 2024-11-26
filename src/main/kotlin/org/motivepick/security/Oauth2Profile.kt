package org.motivepick.security

/**
 * Represents a profile retrieved from an OAuth2 provider like GitHub or VK.
 */
class Oauth2Profile(val id: String, val name: String) {

    companion object {
        fun empty(): Oauth2Profile {
            return Oauth2Profile("", "")
        }
    }
}

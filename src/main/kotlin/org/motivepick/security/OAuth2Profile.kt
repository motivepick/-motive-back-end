package org.motivepick.security

/**
 * Represents a profile retrieved from an OAuth2 provider like GitHub or VK.
 */
class OAuth2Profile(val id: String, val name: String) {

    companion object {

        fun empty(): OAuth2Profile = OAuth2Profile("", "")
    }
}

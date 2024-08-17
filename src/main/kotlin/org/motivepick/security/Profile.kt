package org.motivepick.security

/**
 * Represents a profile retrieved from an OAuth2 provider like GitHub or VK.
 */
class Profile(val id: String, val name: String, val temporary: Boolean)

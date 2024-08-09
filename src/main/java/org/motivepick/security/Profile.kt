package org.motivepick.security

/**
 * Represents a profile retrieved from an OAuth2 provider like Facebook or VK.
 */
class Profile(val id: String, val name: String, val temporary: Boolean)

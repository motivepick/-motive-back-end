package org.motivepick.security

import org.springframework.security.core.AuthenticationException

class UserNotAuthorizedException(msg: String?) : AuthenticationException(msg)
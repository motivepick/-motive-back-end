package org.motivepick.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

internal interface Oauth2LoginService {

    fun login(request: HttpServletRequest, response: HttpServletResponse)

    fun loginCallback(request: HttpServletRequest, response: HttpServletResponse)
}

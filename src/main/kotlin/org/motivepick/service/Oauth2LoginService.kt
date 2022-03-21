package org.motivepick.service

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface Oauth2LoginService {

    fun login(request: HttpServletRequest, response: HttpServletResponse)

    fun loginCallback(request: HttpServletRequest, response: HttpServletResponse)
}

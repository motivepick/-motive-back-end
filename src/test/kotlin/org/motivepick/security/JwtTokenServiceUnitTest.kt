package org.motivepick.security

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class JwtTokenServiceUnitTest {

    @Test
    fun shouldExtractBearerToken() {
        val instanceUnderTest = JwtTokenService("", "")
        val request = request(arrayOf(Cookie("Authorization", "Bearer%09+%0AdGVzdA%3D%3D")))
        assertThat(instanceUnderTest.lookupToken(request), equalTo("dGVzdA=="))
    }

    @Test
    fun shouldReturnEmptyStringForOtherAuthenticationSchemes() {
        val instanceUnderTest = JwtTokenService("", "")
        val request = request(arrayOf(Cookie("Authorization", "Digest+dGVzdA%3D%3D")))
        assertThat(instanceUnderTest.lookupToken(request), equalTo(""))
    }

    @Test
    fun shouldReturnEmptyStringForAbsentAuthorizationCookie() {
        val instanceUnderTest = JwtTokenService("", "")
        val request = request(arrayOf(Cookie("_gat", "1")))
        assertThat(instanceUnderTest.lookupToken(request), equalTo(""))
    }

    private fun request(cookies: Array<Cookie>): HttpServletRequest {
        val request: HttpServletRequest = mock(HttpServletRequest::class.java)
        `when`(request.cookies).thenReturn(cookies)
        return request
    }
}

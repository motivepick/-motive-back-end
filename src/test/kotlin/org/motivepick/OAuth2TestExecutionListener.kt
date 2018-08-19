package org.motivepick

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.test.context.TestContext
import org.springframework.test.context.support.AbstractTestExecutionListener

class OAuth2TestExecutionListener : AbstractTestExecutionListener() {

    override fun beforeTestClass(testContext: TestContext) {
        val integrationTest = testContext.testClass.annotations.find { it is IntegrationTest } as IntegrationTest
        val authorities = setOf<GrantedAuthority>(SimpleGrantedAuthority("ROLE_USER"))
        val attributes = mapOf("id" to integrationTest.userAccountId, "name" to integrationTest.userName)
        val user = DefaultOAuth2User(authorities, attributes, "name")
        SecurityContextHolder.getContext().authentication = OAuth2AuthenticationToken(user, authorities, "junit")
    }
}
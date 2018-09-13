package org.motivepick

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.TestContext
import org.springframework.test.context.support.AbstractTestExecutionListener

class OAuth2TestExecutionListener : AbstractTestExecutionListener() {

    override fun beforeTestClass(testContext: TestContext) {
        val integrationTest = testContext.testClass.annotations.find { it is IntegrationTest } as IntegrationTest
        val authorities = setOf<GrantedAuthority>(SimpleGrantedAuthority("ROLE_USER"))
        val user = UsernamePasswordAuthenticationToken(integrationTest.userAccountId, null, authorities)
        SecurityContextHolder.getContext().authentication = user
    }
}
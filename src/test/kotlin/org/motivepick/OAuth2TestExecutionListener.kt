package org.motivepick

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.TestContext
import org.springframework.test.context.support.AbstractTestExecutionListener

internal class OAuth2TestExecutionListener : AbstractTestExecutionListener() {

    override fun beforeTestClass(testContext: TestContext) {
        val userAnnotationOrNull: Annotation? = testContext.testClass.annotations.find { it is User }
        if (userAnnotationOrNull == null) {
            throw IllegalStateException("The test class is missing the ${User::class.java.name} annotation")
        }
        val userAnnotation = userAnnotationOrNull as User
        val authorities = setOf<GrantedAuthority>(SimpleGrantedAuthority("ROLE_USER"))
        val user = UsernamePasswordAuthenticationToken(userAnnotation.userAccountId, null, authorities)
        SecurityContextHolder.getContext().authentication = user
    }
}

package org.motivepick

import com.github.springtestdbunit.DbUnitTestExecutionListener
import jakarta.transaction.Transactional
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.bean.override.mockito.MockitoResetTestExecutionListener
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.transaction.TransactionalTestExecutionListener

@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@TestExecutionListeners(
    DbUnitTestExecutionListener::class,
    MockitoResetTestExecutionListener::class,
    DependencyInjectionTestExecutionListener::class,
    DirtiesContextTestExecutionListener::class,
    TransactionalTestExecutionListener::class,
    OAuth2TestExecutionListener::class
)
internal annotation class IntegrationTest(val userAccountId: Long)

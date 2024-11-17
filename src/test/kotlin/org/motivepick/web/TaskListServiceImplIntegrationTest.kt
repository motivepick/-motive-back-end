package org.motivepick.web

import com.github.springtestdbunit.annotation.DatabaseOperation.DELETE_ALL
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.motivepick.IntegrationTest
import org.motivepick.domain.entity.TaskListType
import org.motivepick.repository.TaskListRepository
import org.motivepick.service.TaskListService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

@Disabled
@ExtendWith(SpringExtension::class)
@IntegrationTest(1234567890L)
@DatabaseSetup("/dbunit/tasks.xml")
@DatabaseTearDown("/dbunit/tasks.xml", type = DELETE_ALL)
@DbUnitConfiguration(databaseConnection = ["dbUnitDatabaseConnection"])
class TaskListServiceImplIntegrationTest {

    companion object {
        const val INBOX: String = "INBOX"
        const val CLOSED: String = "CLOSED"
    }

    @Autowired
    private lateinit var taskListRepository: TaskListRepository

    @Autowired
    private lateinit var instanceUnderTest: TaskListService

    @Test
    fun `should move task to same list be idempotent`() {
        val mainThreadSecurityContext = SecurityContextHolder.getContext()
        val latch = CountDownLatch(1)
        val thread0 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.moveTask(INBOX, 2, INBOX, 0, 0, latch)
            latch.countDown()
        }
        val thread1 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.moveTask(INBOX, 2, INBOX, 0, 1, latch)
        }
        thread0.join()
        thread1.join()
        val orderedIds = taskListRepository.findByUserAccountIdAndType(1234567890L.toString(), TaskListType.INBOX)!!.orderedIds
        assertThat(orderedIds).isEqualTo(listOf(2L))
    }

    @Test
    fun `should move task to different list be idempotent`() {
        val mainThreadSecurityContext = SecurityContextHolder.getContext()
        val latch = CountDownLatch(1)
        val thread0 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.moveTask(INBOX, 2, CLOSED, 0, 0, latch)
            latch.countDown()
        }
        val thread1 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.moveTask(INBOX, 2, CLOSED, 0, 1, latch)
        }
        thread0.join()
        thread1.join()
        val orderedIds = taskListRepository.findByUserAccountIdAndType(1234567890L.toString(), TaskListType.CLOSED)!!.orderedIds
        assertThat(orderedIds).isEqualTo(listOf(2L, 3L))
    }

    @Test
    fun `should close task be idempotent`() {
        val mainThreadSecurityContext = SecurityContextHolder.getContext()
        val latch = CountDownLatch(1)
        val thread0 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.closeTask(2, 0, latch)
            latch.countDown()
        }
        val thread1 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.closeTask(2, 1, latch)
        }
        thread0.join()
        thread1.join()
        val orderedIds = taskListRepository.findByUserAccountIdAndType(1234567890L.toString(), TaskListType.CLOSED)!!.orderedIds
        assertThat(orderedIds).isEqualTo(listOf(2L, 3L))
    }

    @Test
    fun `should reopen task be idempotent`() {
        val mainThreadSecurityContext = SecurityContextHolder.getContext()
        val latch = CountDownLatch(1)
        val thread0 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.reopenTask(3, 0, latch)
            latch.countDown()
        }
        val thread1 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.reopenTask(3, 1, latch)
        }
        thread0.join()
        thread1.join()
        val orderedIds = taskListRepository.findByUserAccountIdAndType(1234567890L.toString(), TaskListType.INBOX)!!.orderedIds
        assertThat(orderedIds).isEqualTo(listOf(3L, 2L))
    }
}

package org.motivepick.service

import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseOperation
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.motivepick.domain.entity.TaskListType
import org.motivepick.repository.TaskListRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

@Disabled
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@TestExecutionListeners(
    listeners = [DbUnitTestExecutionListener::class],
    mergeMode = MERGE_WITH_DEFAULTS
)
@WithMockUser("1234567890")
@DatabaseSetup("/dbunit/tasks.xml")
@DatabaseTearDown("/dbunit/tasks.xml", type = DatabaseOperation.DELETE_ALL)
@DbUnitConfiguration(databaseConnection = ["dbUnitDatabaseConnection"])
class TaskListServiceImplIntegrationTest {

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
            instanceUnderTest.moveTask(TaskListType.INBOX, 1002, TaskListType.INBOX, 0, 0, latch)
            latch.countDown()
        }
        val thread1 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.moveTask(TaskListType.INBOX, 1002, TaskListType.INBOX, 0, 1, latch)
        }
        thread0.join()
        thread1.join()
        val orderedIds = taskListRepository.findByUserAccountIdAndType(1234567890L.toString(), TaskListType.INBOX)!!.orderedIds
        assertThat(orderedIds).isEqualTo(listOf(1002L))
    }

    @Test
    fun `should move task to different list be idempotent`() {
        val mainThreadSecurityContext = SecurityContextHolder.getContext()
        val latch = CountDownLatch(1)
        val thread0 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.moveTask(TaskListType.INBOX, 1002, TaskListType.CLOSED, 0, 0, latch)
            latch.countDown()
        }
        val thread1 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.moveTask(TaskListType.INBOX, 1002, TaskListType.CLOSED, 0, 1, latch)
        }
        thread0.join()
        thread1.join()
        val orderedIds = taskListRepository.findByUserAccountIdAndType(1234567890L.toString(), TaskListType.CLOSED)!!.orderedIds
        assertThat(orderedIds).isEqualTo(listOf(1002L, 1003L))
    }

    @Test
    fun `should close task be idempotent`() {
        val mainThreadSecurityContext = SecurityContextHolder.getContext()
        val latch = CountDownLatch(1)
        val thread0 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.closeTask(1002, 0, latch)
            latch.countDown()
        }
        val thread1 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.closeTask(1002, 1, latch)
        }
        thread0.join()
        thread1.join()
        val orderedIds = taskListRepository.findByUserAccountIdAndType(1234567890L.toString(), TaskListType.CLOSED)!!.orderedIds
        assertThat(orderedIds).isEqualTo(listOf(1002L, 1003L))
    }

    @Test
    fun `should reopen task be idempotent`() {
        val mainThreadSecurityContext = SecurityContextHolder.getContext()
        val latch = CountDownLatch(1)
        val thread0 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.reopenTask(1003, 0, latch)
            latch.countDown()
        }
        val thread1 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.reopenTask(1003, 1, latch)
        }
        thread0.join()
        thread1.join()
        val orderedIds = taskListRepository.findByUserAccountIdAndType(1234567890L.toString(), TaskListType.INBOX)!!.orderedIds
        assertThat(orderedIds).isEqualTo(listOf(1003L, 1002L))
    }
}

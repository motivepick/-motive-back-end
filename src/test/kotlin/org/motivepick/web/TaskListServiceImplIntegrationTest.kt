package org.motivepick.web

import com.github.springtestdbunit.annotation.DatabaseOperation.DELETE_ALL
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import com.github.springtestdbunit.annotation.DbUnitConfiguration
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

@ExtendWith(SpringExtension::class)
@IntegrationTest(1234567890L)
@DatabaseSetup("/dbunit/tasks.xml")
@DatabaseTearDown("/dbunit/tasks.xml", type = DELETE_ALL)
@DbUnitConfiguration(databaseConnection = ["dbUnitDatabaseConnection"])
class TaskListServiceImplIntegrationTest {

    @Autowired
    private lateinit var taskListRepository: TaskListRepository

    @Autowired
    private lateinit var instanceUnderTest: TaskListService

    @Test
    fun move() {
        val mainThreadSecurityContext = SecurityContextHolder.getContext()
        val latch = CountDownLatch(2)
        val thread1 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.closeTask(2, 0)
        }
        val thread2 = thread {
            SecurityContextHolder.setContext(mainThreadSecurityContext)
            instanceUnderTest.closeTask(2, 1)
        }
        thread1.join()
        thread2.join()
        val orderedIds = taskListRepository.findByUserAccountIdAndType(1234567890L.toString(), TaskListType.CLOSED)!!.orderedIds
        assert(orderedIds.size == 2)
    }
}

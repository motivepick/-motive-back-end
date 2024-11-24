package org.motivepick.web

import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseOperation.DELETE_ALL
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jakarta.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.motivepick.domain.entity.TaskListType
import org.motivepick.domain.view.CreateTaskRequest
import org.motivepick.domain.view.UpdateTaskRequest
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.bean.override.mockito.MockitoResetTestExecutionListener
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import java.time.LocalDateTime
import java.time.ZoneOffset

@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@TestExecutionListeners(
    listeners = [
        DbUnitTestExecutionListener::class,
        MockitoResetTestExecutionListener::class,
        DependencyInjectionTestExecutionListener::class,
        DirtiesContextTestExecutionListener::class,
        TransactionalTestExecutionListener::class],
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@WithMockUser("1234567890")
@DatabaseSetup("/dbunit/tasks.xml")
@DatabaseTearDown("/dbunit/tasks.xml", type = DELETE_ALL)
@DbUnitConfiguration(databaseConnection = ["dbUnitDatabaseConnection"])
class TaskControllerIntegrationTest {

    @Autowired
    private lateinit var controller: TaskController

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun create() {
        val accountId = "1234567890"
        userRepository.findByAccountId("1234567890")

        val request = CreateTaskRequest("\n some task")
        request.description = "  some description "
        val now = LocalDateTime.now()
        request.dueDate = now
        val response = controller.create(request)

        assertThat(response.statusCode, equalTo(HttpStatus.CREATED))

        val task = response.body!!
        assertThat(task.id, notNullValue())
        assertThat(task.closed, equalTo(false))
        assertThat(task.name, equalTo("some task"))
        assertThat(task.description, equalTo("some description"))
        assertThat(task.dueDate, equalTo(now.atOffset(ZoneOffset.UTC)))

        val taskFromDb = taskRepository.findById(task.id).get()
        assertThat(taskFromDb.id, notNullValue())
        assertThat(taskFromDb.created, notNullValue())
        assertThat(taskFromDb.user.accountId, equalTo(accountId))
        assertThat(taskFromDb.closed, equalTo(false))
        assertThat(taskFromDb.name, equalTo("some task"))
        assertThat(taskFromDb.description, equalTo("some description"))
        assertThat(taskFromDb.dueDate, equalTo(request.dueDate))
        assertThat(taskFromDb.taskList?.type, equalTo(TaskListType.INBOX))
    }

    @Test
    fun readNotFound() {
        val response = controller.read(1000L)
        assertThat(response.statusCode, equalTo(HttpStatus.NOT_FOUND))
        assertThat(response.body, nullValue())
    }

    @Test
    fun read() {
        val task = controller.read(2L).body!!
        assertThat(task.id, equalTo(2L))
        assertThat(task.name, equalTo("Test task"))
        assertThat(task.description, equalTo("Test Description"))
        assertThat(task.closed, equalTo(false))
        assertThat(task.dueDate, equalTo(LocalDateTime.of(2019, 1, 2, 0, 0, 0, 0).atOffset(ZoneOffset.UTC)))
    }

    @Test
    fun updateTaskNotFound() {
        val response = controller.update(100L, UpdateTaskRequest())

        assertThat(response.statusCode, equalTo(HttpStatus.NOT_FOUND))
        assertThat(response.body, nullValue())
    }

    @Test
    fun update() {
        val request = UpdateTaskRequest()
        request.name = " some new name\n\t "
        request.description = "  some new description"
        request.closed = true
        val now = LocalDateTime.now()
        request.dueDate = now

        val task = controller.update(2L, request).body!!

        assertThat(task.id, equalTo(2L))
        assertThat(task.name, equalTo("some new name"))
        assertThat(task.description, equalTo("some new description"))
        assertThat(task.closed, equalTo(request.closed))
        assertThat(task.dueDate, equalTo(now.atOffset(ZoneOffset.UTC)))

        val taskFromDb = taskRepository.findById(2L).get()

        assertThat(taskFromDb.name, equalTo("some new name"))
        assertThat(taskFromDb.description, equalTo("some new description"))
        assertThat(taskFromDb.closed, equalTo(request.closed))
        assertThat(taskFromDb.dueDate, equalTo(request.dueDate))
    }

    @Test
    fun deleteNotFound() {
        val response = controller.delete(100L)

        assertThat(response.statusCode, equalTo(HttpStatus.NOT_FOUND))
        assertThat(response.body, nullValue())
    }

    @Test
    fun delete() {
        val response = controller.delete(2L)

        assertThat(response.statusCode, equalTo(HttpStatus.OK))

        val visible = taskRepository
            .findById(2L)
            .map { it.visible }
            .orElseThrow { AssertionError("task should still exist, just be marked invisible") }
        assertThat(visible, equalTo(false))
    }
}

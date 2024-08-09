package org.motivepick.web

import com.github.springtestdbunit.annotation.DatabaseOperation.DELETE_ALL
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.motivepick.IntegrationTest
import org.motivepick.domain.entity.TaskListType
import org.motivepick.domain.ui.task.CreateTaskRequest
import org.motivepick.domain.ui.task.UpdateTaskRequest
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@IntegrationTest(1234567890L)
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
        request.dueDate = LocalDateTime.now()
        val response = controller.create(request)

        assertThat(response.statusCode, equalTo(HttpStatus.CREATED))

        val task = response.body!!
        assertThat(task.id, notNullValue())
        assertThat(task.created, notNullValue())
        assertThat(task.user.accountId, equalTo(accountId))
        assertThat(task.closed, equalTo(false))
        assertThat(task.name, equalTo("some task"))
        assertThat(task.description, equalTo("some description"))
        assertThat(task.dueDate, equalTo(request.dueDate))
        assertThat(task.taskList?.type, equalTo(TaskListType.INBOX))

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
        assertThat(task.created, equalTo(LocalDateTime.of(2018, 8, 11, 19, 55, 47, 900000000)))
        assertThat(task.description, equalTo("Test Description"))
        assertThat(task.closed, equalTo(false))
        assertThat(task.dueDate, equalTo(LocalDateTime.of(2019, 1, 2, 0, 0, 0, 0)))
        assertThat(task.user.id, equalTo(1L))
        assertThat(task.taskList!!.id, equalTo(1L))
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
        request.dueDate = LocalDateTime.now()

        val task = controller.update(2L, request).body!!

        assertThat(task.id, equalTo(2L))
        assertThat(task.name, equalTo("some new name"))
        assertThat(task.description, equalTo("some new description"))
        assertThat(task.closed, equalTo(request.closed))
        assertThat(task.dueDate, equalTo(request.dueDate))

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

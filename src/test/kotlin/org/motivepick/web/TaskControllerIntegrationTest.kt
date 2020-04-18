package org.motivepick.web

import com.github.springtestdbunit.annotation.DatabaseOperation.DELETE_ALL
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.motivepick.IntegrationTest
import org.motivepick.domain.ui.task.CreateTaskRequest
import org.motivepick.domain.ui.task.UpdateTaskRequest
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@IntegrationTest(1234567890L, "Firstname Lastname")
@DatabaseSetup("/dbunit/tasks.xml")
@DatabaseTearDown("/dbunit/tasks.xml", type = DELETE_ALL)
class TaskControllerIntegrationTest {

    @Autowired
    private lateinit var controller: TaskController

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    @Ignore
    fun create() {
        val accountId = "1234567890"
        userRepository.findByAccountId("1234567890")

        val request = CreateTaskRequest("\n some task")
        request.description = "  some description "
        request.dueDate = LocalDateTime.now()
        val response = controller.create(request)

        assertEquals(HttpStatus.CREATED, response.statusCode)

        val task = response.body!!
        assertNotNull(task.id)
        assertNotNull(task.created)
        assertEquals(accountId, task.user.accountId)
        assertEquals(false, task.closed)
        assertEquals("some task", task.name)
        assertEquals("some description", task.description)
        assertEquals(request.dueDate, task.dueDate)
        assertNull(task.taskList)

        val taskFromDb = taskRepository.findById(task.id!!).get()
        assertNotNull(taskFromDb.id)
        assertNotNull(taskFromDb.created)
        assertEquals(accountId, taskFromDb.user.accountId)
        assertEquals(false, taskFromDb.closed)
        assertEquals("some task", taskFromDb.name)
        assertEquals("some description", taskFromDb.description)
        assertEquals(request.dueDate, taskFromDb.dueDate)
        assertNull(taskFromDb.taskList)
    }

    @Test
    fun readNotFound() {
        val response = controller.read(1000L)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    @Ignore
    fun read() {
        val task = controller.read(1L).body!!

        assertEquals(1L, task.id)
        assertEquals("Test task", task.name)
        assertEquals(LocalDateTime.of(2018, 8, 11,
                19, 55, 47, 900000000), task.created)
        assertEquals("Test Description", task.description)
        assertEquals(false, task.closed)
        assertEquals(LocalDateTime.of(2019, 1, 2,
                0, 0, 0, 0), task.dueDate)
        assertEquals(1L, task.user.id)
        assertEquals(1L, task.taskList!!.id)
    }

    @Test
    fun updateTaskNotFound() {
        val response = controller.update(100L, UpdateTaskRequest())

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun update() {
        val request = UpdateTaskRequest()
        request.name = " some new name\n\t "
        request.description = "  some new description"
        request.closed = true
        request.dueDate = LocalDateTime.now()

        val task = controller.update(1L, request).body!!

        assertEquals(1L, task.id)
        assertEquals("some new name", task.name)
        assertEquals("some new description", task.description)
        assertEquals(request.closed, task.closed)
        assertEquals(request.dueDate, task.dueDate)

        val taskFromDb = taskRepository.findById(1L).get()

        assertEquals("some new name", taskFromDb.name)
        assertEquals("some new description", taskFromDb.description)
        assertEquals(request.closed, taskFromDb.closed)
        assertEquals(request.dueDate, taskFromDb.dueDate)
    }

    @Test
    fun deleteNotFound() {
        val response = controller.delete(100L)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun delete() {
        val response = controller.delete(1L)

        assertEquals(HttpStatus.OK, response.statusCode)

        val visible = taskRepository
                .findById(1L)
                .map { it.visible }
                .orElseThrow { AssertionError("task should still exist, just be marked invisible") }
        assertFalse(visible)
    }
}

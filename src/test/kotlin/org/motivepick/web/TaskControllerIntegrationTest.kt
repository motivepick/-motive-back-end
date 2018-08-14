package org.motivepick.web

import com.github.springtestdbunit.annotation.DatabaseOperation
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.motivepick.IntegrationTest
import org.motivepick.domain.ui.task.CreateTaskRequest
import org.motivepick.domain.ui.task.UpdateTaskRequest
import org.motivepick.repository.TaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@IntegrationTest
@DatabaseSetup("/dbunit/tasks.xml")
@DatabaseTearDown("/dbunit/tasks.xml", type = DatabaseOperation.DELETE_ALL)
class TaskControllerIntegrationTest {

    @Autowired
    private lateinit var controller: TaskController

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @Test
    fun createUserNotFound() {
        val request = CreateTaskRequest(12345L, "some task")
        val response = controller.create(request)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun create() {
        val request = CreateTaskRequest(1234567890L, "some task")
        request.description = "some description"
        request.dueDate = LocalDateTime.now()
        val response = controller.create(request)

        assertEquals(HttpStatus.CREATED, response.statusCode)

        val task = response.body!!
        assertNotNull(task.id)
        assertNotNull(task.created)
        assertEquals(request.accountId, task.user.accountId)
        assertEquals(false, task.closed)
        assertEquals(request.name, task.name)
        assertEquals(request.description, task.description)
        assertEquals(request.dueDate, task.dueDate)
        assertNull(task.goal)

        val taskFromDb = taskRepository.findById(task.id!!).get()
        assertNotNull(taskFromDb.id)
        assertNotNull(taskFromDb.created)
        assertEquals(request.accountId, taskFromDb.user.accountId)
        assertEquals(false, taskFromDb.closed)
        assertEquals(request.name, taskFromDb.name)
        assertEquals(request.description, taskFromDb.description)
        assertEquals(request.dueDate, taskFromDb.dueDate)
        assertNull(taskFromDb.goal)
    }

    @Test
    fun listOpened() {
        val tasks = controller.list(1234567890L, true).body!!

        assertEquals(1, tasks.size)
        assertEquals(1L, tasks[0].id)
    }

    @Test
    fun listClosed() {
        val tasks = controller.list(1234567890L, false).body!!

        assertEquals(1, tasks.size)
        assertEquals(2L, tasks[0].id)
    }

    @Test
    fun readNotFound() {
        val response = controller.read(1000L)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
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
        assertEquals(1L, task.goal!!.id)
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
        request.name = "some new name"
        request.description = "some new description"
        request.closed = true
        request.dueDate = LocalDateTime.now()

        val task = controller.update(1L, request).body!!

        assertEquals(1L, task.id)
        assertEquals(request.name, task.name)
        assertEquals(request.description, task.description)
        assertEquals(request.closed, task.closed)
        assertEquals(request.dueDate, task.dueDate)

        val taskFromDb = taskRepository.findById(1L).get()

        assertEquals(request.name, taskFromDb.name)
        assertEquals(request.description, taskFromDb.description)
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

        assertFalse(taskRepository.existsById(1L))
    }
}
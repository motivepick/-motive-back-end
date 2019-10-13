package org.motivepick.web

import com.github.springtestdbunit.annotation.DatabaseOperation
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.motivepick.IntegrationTest
import org.motivepick.domain.ui.goal.CreateGoalRequest
import org.motivepick.domain.ui.goal.UpdateGoalRequest
import org.motivepick.repository.GoalRepository
import org.motivepick.repository.TaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@IntegrationTest(1234567890L, "Firstname Lastname")
@DatabaseSetup("/dbunit/goals.xml")
@DatabaseTearDown("/dbunit/goals.xml", type = DatabaseOperation.DELETE_ALL)
class GoalControllerIntegrationTest {

    @Autowired
    private lateinit var controller: GoalController

    @Autowired
    private lateinit var goalRepository: GoalRepository

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @Test
    fun create() {
        val accountId = "1234567890"

        val request = CreateGoalRequest("some goal")
        request.description = "some description"
        request.dueDate = LocalDateTime.now()
        val response = controller.create(request)

        assertEquals(HttpStatus.CREATED, response.statusCode)

        val goal = response.body!!
        assertNotNull(goal.id)
        assertNotNull(goal.created)
        assertEquals(accountId, goal.user.accountId)
        assertEquals(false, goal.closed)
        assertEquals(request.name, goal.name)
        assertEquals(request.description, goal.description)
        assertEquals(request.dueDate, goal.dueDate)

        val goalFromDb = goalRepository.findById(goal.id!!).get()
        assertNotNull(goalFromDb.id)
        assertNotNull(goalFromDb.created)
        assertEquals(accountId, goalFromDb.user.accountId)
        assertEquals(false, goalFromDb.closed)
        assertEquals(request.name, goalFromDb.name)
        assertEquals(request.description, goalFromDb.description)
        assertEquals(request.dueDate, goalFromDb.dueDate)
    }

    @Test
    fun readNotFound() {
        val response = controller.read(1000L)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun read() {
        val goal = controller.read(1L).body!!

        assertEquals(1L, goal.id)
        assertEquals("Test goal", goal.name)
        assertEquals(LocalDateTime.of(2018, 8, 11,
                19, 55, 47, 900000000), goal.created)
        assertEquals("Test Description", goal.description)
        assertEquals(false, goal.closed)
        assertEquals(LocalDateTime.of(2019, 1, 2,
                0, 0, 0, 0), goal.dueDate)
    }

    @Test
    fun updateGoalNotFound() {
        val response = controller.update(100L, UpdateGoalRequest())

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun update() {
        val request = UpdateGoalRequest()
        request.name = "some new name"
        request.description = "some new description"
        request.closed = true
        request.dueDate = LocalDateTime.now()

        val goal = controller.update(1L, request).body!!

        assertEquals(1L, goal.id)
        assertEquals(request.name, goal.name)
        assertEquals(request.description, goal.description)
        assertEquals(request.closed, goal.closed)
        assertEquals(request.dueDate, goal.dueDate)

        val goalFromDb = goalRepository.findById(1L).get()

        assertEquals(request.name, goalFromDb.name)
        assertEquals(request.description, goalFromDb.description)
        assertEquals(request.closed, goalFromDb.closed)
        assertEquals(request.dueDate, goalFromDb.dueDate)
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

        assertFalse(goalRepository.existsById(1L))
    }

    @Test
    fun assignGoalNotFound() {
        val response = controller.assign(10L, 2L)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun assignTaskNotFound() {
        val response = controller.assign(1L, 1000L)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun assign() {
        val response = controller.assign(1L, 2L)

        assertEquals(HttpStatus.OK, response.statusCode)

        val goal = goalRepository.findById(1L).get()
        assertEquals(1, goal.tasks.size)
        assertEquals(2L, goal.tasks[0].id)

        val task = taskRepository.findById(2L).get()
        assertEquals(1L, task.goal!!.id)
    }
}
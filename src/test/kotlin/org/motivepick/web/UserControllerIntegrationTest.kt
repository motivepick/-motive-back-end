package org.motivepick.web

import com.github.springtestdbunit.annotation.DatabaseOperation
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.motivepick.IntegrationTest
import org.motivepick.domain.entity.User
import org.motivepick.domain.ui.user.CreateUserRequest
import org.motivepick.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@IntegrationTest
@DatabaseSetup("/dbunit/users.xml")
@DatabaseTearDown("/dbunit/users.xml", type = DatabaseOperation.DELETE_ALL)
class UserControllerIntegrationTest {

    private val ACCOUNT_ID = 1234567890L

    @Autowired
    private lateinit var controller: UserController

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun createUser() {
        val request = CreateUserRequest(987654321L, "Test Account", "Some Token")

        val response = controller.create(request)
        assertEquals(HttpStatus.OK, response.statusCode)

        val user = response.body!!

        assertEquals(request.accountId, user.accountId)
        assertEquals(request.name, user.name)
        assertEquals(request.token, user.token)

        val userFromDb = userRepository.findByAccountId(request.accountId)!!
        assertNotNull(userFromDb)
        assertEquals(request.accountId, userFromDb.accountId)
        assertEquals(request.name, userFromDb.name)
        assertEquals(request.token, userFromDb.token)
    }

    @Test
    fun createToken() {
        val request = CreateUserRequest(ACCOUNT_ID, "Test Account", "New Token")

        val response = controller.create(request)
        assertEquals(HttpStatus.OK, response.statusCode)

        val user = response.body!!

        assertEquals(request.accountId, user.accountId)
        assertEquals(request.token, user.token)
    }

    @Test
    fun read() {
        val response = controller.read(ACCOUNT_ID)
        assertEquals(HttpStatus.OK, response.statusCode)
        val user = response.body!!
        assertExpectedUser(user)
    }

    @Test
    fun readNotFound() {
        val response = controller.read(0)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun readAll() {
        val response = controller.readAll()
        assertEquals(HttpStatus.OK, response.statusCode)

        val iterable = response.body!!
        val list = ArrayList<User>()
        iterable.forEach { list += it }

        assertEquals(1, list.size)
        assertExpectedUser(list[0])
    }

    @Test
    fun revoke() {
        val response = controller.revoke(ACCOUNT_ID)
        assertEquals(HttpStatus.OK, response.statusCode)

        val user = userRepository.findByAccountId(ACCOUNT_ID)!!
        assertNull(user.token)
    }

    @Test
    fun revokeNotFound() {
        val response = controller.revoke(0)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    private fun assertExpectedUser(user: User) {
        assertEquals(1L, user.id)
        assertEquals(ACCOUNT_ID, user.accountId)
        assertEquals("Firstname Lastname", user.name)
        assertEquals("Test-Token", user.token)
    }
}
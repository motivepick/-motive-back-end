package org.motivepick.web

import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseOperation.DELETE_ALL
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jakarta.servlet.http.Cookie
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.motivepick.domain.entity.TaskListType
import org.motivepick.extensions.PathExtensions.readTextFromResource
import org.motivepick.repository.TaskListRepository
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.motivepick.security.github.GitHubProfile
import org.motivepick.security.github.GitHubTokenResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.*
import kotlin.io.path.Path

@ActiveProfiles("test")
@SpringBootTest // @SpringBootTest and @AutoConfigureMockMvc are used in place of @WebMvcTest(GitHubController::class), see its Javadoc as for why.
@AutoConfigureMockMvc
@TestExecutionListeners(
    listeners = [DbUnitTestExecutionListener::class],
    mergeMode = MERGE_WITH_DEFAULTS // To keep the listener that enables the @MockitoBean to work properly.
)
@DatabaseSetup("/dbunit/tasks.xml")
@DatabaseTearDown("/dbunit/tasks.xml", type = DELETE_ALL)
@DbUnitConfiguration(databaseConnection = ["dbUnitDatabaseConnection"])
class GitHubControllerIntegrationTest {

    companion object {
        const val GITHUB_TEMPORARY_CODE = "fe6c0fbf37ba7131524a"
        const val STATE_UUID = "47443547-d27e-4913-9b14-0bf19bfffd51"
        const val NEW_USER_ACCOUNT_ID = 311348659
        const val EXISTING_USER_ACCOUNT_ID = 1234567890
        const val TEMPORARY_USER_ACCOUNT_ID = "265508a4-2c3b-4d03-8eea-c536ad2e6a72"
    }

    @MockitoBean
    private lateinit var httpClient: RestTemplate

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @Autowired
    private lateinit var taskListRepository: TaskListRepository

    @Test
    fun `should create initial tasks when user creates new permanent account`() {
        configureHttpClient(NEW_USER_ACCOUNT_ID)

        val state = Base64.getEncoder().encodeToString(STATE_UUID.toByteArray())
        val requestBuilder = get("/oauth2/authorization/github/callback")
            .param("code", GITHUB_TEMPORARY_CODE)
            .param("state", state)
        mockMvc
            .perform(requestBuilder)
            .andExpect(status().isFound())

        assertThat(userRepository.findByAccountId(NEW_USER_ACCOUNT_ID.toString()), notNullValue())

        val expectedInitialTasks = arrayOf(
            "Buy a birthday present for Steve",
            "Finish the course about microservices",
            "Finalize the blog post",
            "Tidy up the kitchen",
            "Transfer money for the new illustration to Ann",
            "Find a hotel in Sofia",
            "Write a review for the Estonian teacher"
        )
        val tasks = taskRepository.findAllByUserAccountId(NEW_USER_ACCOUNT_ID.toString())
        assertThat(tasks.map { it.name }, containsInAnyOrder(*expectedInitialTasks))
        tasks.forEach { task -> assertThat(task.user.accountId, equalTo(NEW_USER_ACCOUNT_ID.toString())) }
    }

    @Test
    fun `should migrate tasks when temporary user that has no permanent account creates new permanent account`() {
        configureHttpClient(NEW_USER_ACCOUNT_ID)

        val temporaryUserToken = Path("token.$TEMPORARY_USER_ACCOUNT_ID.txt").readTextFromResource()
        val state = Base64.getEncoder().encodeToString(STATE_UUID.toByteArray())
        val requestBuilder = get("/oauth2/authorization/github/callback")
            .cookie(Cookie("Authorization", temporaryUserToken))
            .param("code", GITHUB_TEMPORARY_CODE)
            .param("state", state)
        mockMvc
            .perform(requestBuilder)
            .andExpect(status().isFound())

        assertThat(userRepository.findByAccountId(TEMPORARY_USER_ACCOUNT_ID), nullValue())
        assertThat(userRepository.findByAccountId(NEW_USER_ACCOUNT_ID.toString()), notNullValue())

        val tasks = taskRepository.findAllByUserAccountId(NEW_USER_ACCOUNT_ID.toString())
        assertThat(tasks.map { it.id }, containsInAnyOrder(1005L, 1006L))
        tasks.forEach { task -> assertThat(task.user.accountId, equalTo(NEW_USER_ACCOUNT_ID.toString())) }

        assertThat(taskListRepository.findByUserAccountIdAndType(NEW_USER_ACCOUNT_ID.toString(), TaskListType.INBOX)?.orderedIds, contains(1005L))
        assertThat(taskListRepository.findByUserAccountIdAndType(NEW_USER_ACCOUNT_ID.toString(), TaskListType.CLOSED)?.orderedIds, contains(1006L))
        assertThat(taskListRepository.findByUserAccountIdAndType(NEW_USER_ACCOUNT_ID.toString(), TaskListType.SCHEDULE)?.orderedIds, contains(1005L))
    }

    @Test
    fun `should migrate tasks when temporary user that has permanent account logs in with permanent account`() {
        configureHttpClient(EXISTING_USER_ACCOUNT_ID)

        val temporaryUserToken = Path("token.$TEMPORARY_USER_ACCOUNT_ID.txt").readTextFromResource()
        val state = Base64.getEncoder().encodeToString(STATE_UUID.toByteArray())
        val requestBuilder = get("/oauth2/authorization/github/callback")
            .cookie(Cookie("Authorization", temporaryUserToken))
            .param("code", GITHUB_TEMPORARY_CODE)
            .param("state", state)
        mockMvc
            .perform(requestBuilder)
            .andExpect(status().isFound())

        assertThat(userRepository.findByAccountId(TEMPORARY_USER_ACCOUNT_ID), nullValue())
        assertThat(userRepository.findByAccountId(EXISTING_USER_ACCOUNT_ID.toString()), notNullValue())

        val tasks = taskRepository.findAllByUserAccountId(EXISTING_USER_ACCOUNT_ID.toString())
        assertThat(tasks.map { it.id }, containsInAnyOrder(1002L, 1003L, 1005L, 1006L))
        tasks.forEach { task -> assertThat(task.user.accountId, equalTo(EXISTING_USER_ACCOUNT_ID.toString())) }

        assertThat(taskListRepository.findByUserAccountIdAndType(EXISTING_USER_ACCOUNT_ID.toString(), TaskListType.INBOX)?.orderedIds, contains(1005L, 1002L))
        assertThat(taskListRepository.findByUserAccountIdAndType(EXISTING_USER_ACCOUNT_ID.toString(), TaskListType.CLOSED)?.orderedIds, contains(1006L, 1003L))
        assertThat(taskListRepository.findByUserAccountIdAndType(EXISTING_USER_ACCOUNT_ID.toString(), TaskListType.SCHEDULE)?.orderedIds, contains(1005L, 1002L))
    }

    private fun configureHttpClient(persistentAccountId: Int) {
        val fetchTokenUri: URI = UriComponentsBuilder.fromUriString("https://github.com/login/oauth/access_token")
            .queryParam("client_id", "testClientId")
            .queryParam("client_secret", "testClientSecret")
            .queryParam("code", GITHUB_TEMPORARY_CODE)
            .queryParam("redirect_uri", "https://localhost/oauth2/authorization/github/callback")
            .build()
            .toUri()
        val fetchProfileUri: URI = UriComponentsBuilder.fromUriString("https://api.github.com/user")
            .build()
            .toUri()
        `when`(httpClient.exchange(eq(fetchTokenUri), any(HttpMethod::class.java), any(HttpEntity::class.java), any(GitHubTokenResponse::class.java::class.java)))
            .thenReturn(ResponseEntity(GitHubTokenResponse("secret"), HttpStatus.OK))
        `when`(httpClient.exchange(eq(fetchProfileUri), any(HttpMethod::class.java), any(HttpEntity::class.java), any(GitHubProfile::class.java::class.java)))
            .thenReturn(ResponseEntity(GitHubProfile(persistentAccountId, "yaskovdev"), HttpStatus.OK))
    }
}

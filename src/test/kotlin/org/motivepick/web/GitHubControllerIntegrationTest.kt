package org.motivepick.web

import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseOperation.DELETE_ALL
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.motivepick.OAuth2TestExecutionListener
import org.motivepick.User
import org.motivepick.domain.entity.TaskListType
import org.motivepick.repository.TaskListRepository
import org.motivepick.repository.TaskRepository
import org.motivepick.repository.UserRepository
import org.motivepick.security.github.GitHubProfile
import org.motivepick.security.github.GitHubTokenResponse
import org.motivepick.web.GitHubControllerIntegrationTest.Companion.TEMPORARY_USER_ACCOUNT_ID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.*

@ActiveProfiles("test")
@SpringBootTest // @SpringBootTest and @AutoConfigureMockMvc are used in place of @WebMvcTest(GitHubController::class), see its Javadoc as for why.
@AutoConfigureMockMvc
@TestExecutionListeners(
    listeners = [DbUnitTestExecutionListener::class, OAuth2TestExecutionListener::class], // OAuth2TestExecutionListener sets the account id from @User to security context
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS // To keep the listener that enables the @MockitoBean to work properly.
)
@DatabaseSetup("/dbunit/tasks.xml")
@DatabaseTearDown("/dbunit/tasks.xml", type = DELETE_ALL)
@DbUnitConfiguration(databaseConnection = ["dbUnitDatabaseConnection"])
@User(TEMPORARY_USER_ACCOUNT_ID)
class GitHubControllerIntegrationTest {

    companion object {
        const val GITHUB_TEMPORARY_CODE = "123"
        const val STATE_UUID = "47443547-d27e-4913-9b14-0bf19bfffd51"
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
    fun `should migrate tasks when temporary user logs in with permanent account`() {
        configureHttpClient()

        val state = Base64.getEncoder().encodeToString(STATE_UUID.toByteArray())
        val requestBuilder = get("/oauth2/authorization/github/callback")
            .param("code", GITHUB_TEMPORARY_CODE)
            .param("state", state)
        mockMvc
            .perform(requestBuilder)
            .andExpect(status().isFound())

        assertThat(userRepository.findByAccountId(TEMPORARY_USER_ACCOUNT_ID), nullValue())
        assertThat(userRepository.findByAccountId(EXISTING_USER_ACCOUNT_ID.toString()), notNullValue())

        val tasks = taskRepository.findAllByUserAccountId(EXISTING_USER_ACCOUNT_ID.toString())
        assertThat(tasks.map { it.id }, containsInAnyOrder(2L, 3L, 5L, 6L))
        tasks.forEach { task -> assertThat(task.user.accountId, equalTo(EXISTING_USER_ACCOUNT_ID.toString())) }

        assertThat(taskListRepository.findByUserAccountIdAndType(EXISTING_USER_ACCOUNT_ID.toString(), TaskListType.INBOX)?.orderedIds, contains(5L, 2L))
        assertThat(taskListRepository.findByUserAccountIdAndType(EXISTING_USER_ACCOUNT_ID.toString(), TaskListType.CLOSED)?.orderedIds, contains(6L, 3L))
        assertThat(taskListRepository.findByUserAccountIdAndType(EXISTING_USER_ACCOUNT_ID.toString(), TaskListType.SCHEDULE)?.orderedIds, contains(5L, 2L))
    }

    private fun configureHttpClient() {
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
            .thenReturn(ResponseEntity(GitHubTokenResponse("abc"), HttpStatus.OK))
        `when`(httpClient.exchange(eq(fetchProfileUri), any(HttpMethod::class.java), any(HttpEntity::class.java), any(GitHubProfile::class.java::class.java)))
            .thenReturn(ResponseEntity(GitHubProfile(EXISTING_USER_ACCOUNT_ID, "yaskovdev"), HttpStatus.OK))
    }
}

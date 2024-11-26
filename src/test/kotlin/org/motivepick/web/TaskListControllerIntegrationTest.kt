package org.motivepick.web

import com.github.springtestdbunit.DbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseOperation.DELETE_ALL
import com.github.springtestdbunit.annotation.DatabaseSetup
import com.github.springtestdbunit.annotation.DatabaseTearDown
import com.github.springtestdbunit.annotation.DbUnitConfiguration
import jakarta.servlet.http.Cookie
import jakarta.transaction.Transactional
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.motivepick.extensions.PathExtensions.readTextFromResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.io.path.Path

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@TestExecutionListeners(
    listeners = [DbUnitTestExecutionListener::class],
    mergeMode = MERGE_WITH_DEFAULTS
)
@DatabaseSetup("/dbunit/tasks.xml")
@DatabaseTearDown("/dbunit/tasks.xml", type = DELETE_ALL)
@DbUnitConfiguration(databaseConnection = ["dbUnitDatabaseConnection"])
@AutoConfigureMockMvc
class TaskListControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should create a custom task list if the user exists`() {
        val token = Path("token.aae47dd3-32f1-415d-8bd8-4dc1086a6d10.txt").readTextFromResource()
        mockMvc
            .perform(post("/task-lists").cookie(Cookie("Authorization", token)))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("{\"id\":1,\"type\":\"CUSTOM\"}")))
    }

    @Test
    fun `should respond with 404 if the user does not exist`() {
        val token = Path("token.c03354bb-0c31-4010-90a1-65582f4c35cf.txt").readTextFromResource()
        mockMvc
            .perform(post("/task-lists").cookie(Cookie("Authorization", token)))
            .andExpect(status().isNotFound())
    }

    @Test
    fun `should read tasks by task list ID`() {
        val token = Path("token.aae47dd3-32f1-415d-8bd8-4dc1086a6d10.txt").readTextFromResource()
        mockMvc
            .perform(get("/task-lists/1005").param("offset", "0").param("limit", "1").cookie(Cookie("Authorization", token)))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("{\"content\":[{\"id\":1004,\"name\":\"Test task 3\",\"description\":\"\",\"dueDate\":null,\"closed\":false}],\"page\":{\"size\":1,\"number\":0,\"totalElements\":1,\"totalPages\":1}}")))
    }

    @Test
    fun `should read tasks by a predefined task list type`() {
        val token = Path("token.aae47dd3-32f1-415d-8bd8-4dc1086a6d10.txt").readTextFromResource()
        mockMvc
            .perform(get("/task-lists/INBOX").param("offset", "0").param("limit", "1").cookie(Cookie("Authorization", token)))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("{\"content\":[{\"id\":1004,\"name\":\"Test task 3\",\"description\":\"\",\"dueDate\":null,\"closed\":false}],\"page\":{\"size\":1,\"number\":0,\"totalElements\":1,\"totalPages\":1}}")))
    }

    @Test
    fun `should return 400 if the task list type is not predefined`() {
        val token = Path("token.aae47dd3-32f1-415d-8bd8-4dc1086a6d10.txt").readTextFromResource()
        mockMvc
            .perform(get("/task-lists/CUSTOM").param("offset", "0").param("limit", "1").cookie(Cookie("Authorization", token)))
            .andExpect(status().is4xxClientError())
    }

    @Test
    fun `should return 404 if task list does not exist`() {
        val token = Path("token.aae47dd3-32f1-415d-8bd8-4dc1086a6d10.txt").readTextFromResource()
        mockMvc
            .perform(get("/task-lists/1000000").param("offset", "0").param("limit", "1").cookie(Cookie("Authorization", token)))
            .andExpect(status().isNotFound())
    }
}

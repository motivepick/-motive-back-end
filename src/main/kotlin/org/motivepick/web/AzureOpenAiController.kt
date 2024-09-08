package org.motivepick.web

import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.ai.openai.models.ChatCompletionsOptions
import com.azure.ai.openai.models.ChatRequestMessage
import com.azure.ai.openai.models.ChatRequestSystemMessage
import com.azure.ai.openai.models.ChatRequestUserMessage
import com.azure.core.credential.AzureKeyCredential
import org.motivepick.config.AzureOpenAiConfig
import org.motivepick.domain.view.RephraseView
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AzureOpenAiController(private val azureOpenAiConfig: AzureOpenAiConfig) {

    @PostMapping("/rephrase")
    fun rephrase(@RequestBody request: String): ResponseEntity<RephraseView> {
        val azureOpenaiKey: String = azureOpenAiConfig.apiKey
        val endpoint: String = azureOpenAiConfig.endpoint
        val deploymentOrModelId = azureOpenAiConfig.deploymentOrModelId

        val client = OpenAIClientBuilder()
            .endpoint(endpoint)
            .credential(AzureKeyCredential(azureOpenaiKey))
            .buildClient()

        val prompt = this::class.java.classLoader.getResource("azure-open-ai-prompt.txt")?.readText() ?: throw IllegalStateException("Prompt file not found")
        val chatMessages: MutableList<ChatRequestMessage> = ArrayList()
        chatMessages.add(ChatRequestSystemMessage(prompt))
        chatMessages.add(ChatRequestUserMessage(request))

        val chatCompletions = client.getChatCompletions(deploymentOrModelId, ChatCompletionsOptions(chatMessages))

        System.out.printf("Model ID=%s is created at %s.%n", chatCompletions.id, chatCompletions.createdAt)
        for (choice in chatCompletions.choices) {
            val message = choice.message
            System.out.printf("Index: %d, Chat Role: %s.%n", choice.index, message.role)
            println("Message:")
            println(message.content)
        }

        println()
        val usage = chatCompletions.usage
        System.out.printf(
            "Usage: number of prompt token is %d, "
                    + "number of completion token is %d, and number of total tokens in request and response is %d.%n",
            usage.promptTokens, usage.completionTokens, usage.totalTokens
        )

        return ResponseEntity.ok(RephraseView(request, chatCompletions.choices[0].message.content))
    }
}

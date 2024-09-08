package org.motivepick.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class AzureOpenAiConfig (

    @Value("\${azure.openai.apiKey}")
    val apiKey: String,

    @Value("\${azure.openai.endpoint}")
    val endpoint: String,

    @Value("\${azure.openai.deploymentOrModelId}")
    val deploymentOrModelId: String
)

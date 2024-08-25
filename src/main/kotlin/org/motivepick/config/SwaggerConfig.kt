package org.motivepick.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class SwaggerConfig {

    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Milestone Back End API")
                    .description("Milestone Back End")
                    .version("0.0.2-SNAPSHOT")
                    .license(License().name("MIT License").url("https://github.com/motivepick/motive-back-end?tab=MIT-1-ov-file#readme"))
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Milestone Back End Wiki Documentation")
                    .url("https://github.com/motivepick/motive-back-end/wiki")
            )
    }
}

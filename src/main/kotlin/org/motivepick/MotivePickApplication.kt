package org.motivepick

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Clock

@SpringBootApplication
open class MotivePickApplication {

    @Bean
    open fun webMvcConfigurer(): WebMvcConfigurer = object : WebMvcConfigurer {
        override fun addCorsMappings(registry: CorsRegistry) {
            registry
                .addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
        }
    }

    @Bean
    open fun restTemplate(): RestTemplate = RestTemplate()

    @Bean
    open fun clock(): Clock = Clock.systemUTC()
}

fun main(args: Array<String>) {
    SpringApplication.run(MotivePickApplication::class.java, *args)
}

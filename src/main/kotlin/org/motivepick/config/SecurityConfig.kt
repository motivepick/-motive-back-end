package org.motivepick.config

import jakarta.servlet.http.HttpServletRequest
import org.motivepick.security.JwtTokenAuthenticationProcessingFilter
import org.motivepick.security.JwtTokenService
import org.motivepick.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class SecurityConfig {

    companion object {
        private val ANONYMOUS_URIS = arrayOf(
            "/",
            "/oauth2/authorization/facebook",
            "/oauth2/authorization/vk",
            "/temporary/login",
            "/error**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
        )
    }

    @Autowired
    private lateinit var jwtTokenService: JwtTokenService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var cookieFactory: CookieFactory

    @Autowired
    private lateinit var config: ServerConfig

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain = http
        .cors {
            it.configurationSource {
                val configuration = CorsConfiguration()
                configuration.allowCredentials = true
                configuration.allowedOriginPatterns = listOf("*")
                configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
                configuration.allowedHeaders = listOf("*")
                configuration
            }
        }
        .sessionManagement {
            it.sessionCreationPolicy(STATELESS)
        }
        .authorizeHttpRequests {
            it.requestMatchers(*ANONYMOUS_URIS).permitAll().anyRequest().authenticated()
        }
        .addFilterBefore(jwtTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
        .csrf {
            // TODO implement CSRF protection
            it.disable()
        }
        .logout {
            it.addLogoutHandler(CustomLogoutHandler(jwtTokenService, userService, cookieFactory))
                .logoutSuccessUrl(config.logoutSuccessUrl)
        }
        .build()

    private fun jwtTokenAuthenticationProcessingFilter(): JwtTokenAuthenticationProcessingFilter {
        val requestMatcher = object : RequestMatcher {
            val matchers: OrRequestMatcher = OrRequestMatcher(ANONYMOUS_URIS.map { AntPathRequestMatcher(it) })
            val processingMatcher: RequestMatcher = AntPathRequestMatcher("/**")

            override fun matches(request: HttpServletRequest): Boolean {
                return !matchers.matches(request) && processingMatcher.matches(request)
            }
        }
        return JwtTokenAuthenticationProcessingFilter(requestMatcher, jwtTokenService)
    }
}

package org.motivepick.config

import org.motivepick.security.JWT_TOKEN_COOKIE
import org.motivepick.security.JwtTokenAuthenticationProcessingFilter
import org.motivepick.security.JwtTokenFactory
import org.motivepick.web.ServerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.http.HttpServletRequest

@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    companion object {
        private val ANONYMOUS_URIS = arrayOf(
                "/",
                "/oauth2/authorization/facebook",
                "/oauth2/authorization/vk",
                "/temporary/login",
                "/error**")
    }

    @Autowired
    private lateinit var jwtTokenFactory: JwtTokenFactory

    @Autowired
    private lateinit var config: ServerConfig

    override fun configure(http: HttpSecurity) {
        http
                .cors()
                .and().sessionManagement().sessionCreationPolicy(STATELESS)
                .and().authorizeRequests()
                .antMatchers(*ANONYMOUS_URIS).permitAll()
                .anyRequest().authenticated()
                .and().addFilterBefore(jwtTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .csrf().disable() // TODO implement CSRF protection
                .logout().deleteCookies(JWT_TOKEN_COOKIE).logoutSuccessUrl(config.logoutSuccessUrl)
    }

    private fun jwtTokenAuthenticationProcessingFilter(): JwtTokenAuthenticationProcessingFilter {
        val requestMatcher = object : RequestMatcher {
            val matchers: OrRequestMatcher = OrRequestMatcher(ANONYMOUS_URIS.map { AntPathRequestMatcher(it) })
            val processingMatcher: RequestMatcher = AntPathRequestMatcher("/**")

            override fun matches(request: HttpServletRequest): Boolean {
                return !matchers.matches(request) && processingMatcher.matches(request)
            }
        }
        return JwtTokenAuthenticationProcessingFilter(requestMatcher, jwtTokenFactory)
    }
}

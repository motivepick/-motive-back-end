package org.motivepick.config

import org.motivepick.security.MotiveAuthenticationSuccessHandler
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint

@Configuration
class SecurityConfig(private val successHandler: MotiveAuthenticationSuccessHandler) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
                .cors()
                .and().authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling().authenticationEntryPoint(Http403ForbiddenEntryPoint())
                .and().oauth2Login().successHandler(successHandler)
                .and().csrf().disable() // TODO implement CSRF protection
    }
}
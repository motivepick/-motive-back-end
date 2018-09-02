package org.motivepick.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession
import org.springframework.web.filter.CompositeFilter
import javax.servlet.Filter


@Configuration
@EnableOAuth2Client
@EnableJdbcHttpSession
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    @Qualifier("facebookOAuthFilter")
    private lateinit var facebookOAuthFilter: Filter

    override fun configure(http: HttpSecurity) {
        http
                .cors()
                .and().authorizeRequests()
                .antMatchers("/", "/oauth2/authorization**", "/error**")
                .permitAll()

                // see CurrentUser#accountIdFromRequestHeader, delete as soon as mobile OAuth is done
                .antMatchers(HttpMethod.GET).permitAll().antMatchers(HttpMethod.POST).permitAll().antMatchers(HttpMethod.PUT).permitAll().antMatchers(HttpMethod.DELETE).permitAll()

                .anyRequest().authenticated()
                .and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter::class.java)
                .csrf().disable() // TODO implement CSRF protection
    }

    private fun ssoFilter(): Filter {
        val filter = CompositeFilter()
        filter.setFilters(listOf(facebookOAuthFilter))
        return filter
    }

    @Bean
    fun oauth2ClientFilterRegistration(filter: OAuth2ClientContextFilter): FilterRegistrationBean<OAuth2ClientContextFilter> {
        val registration = FilterRegistrationBean<OAuth2ClientContextFilter>()
        registration.filter = filter
        registration.order = -100
        return registration
    }
}

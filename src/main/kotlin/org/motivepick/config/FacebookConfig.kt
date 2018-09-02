package org.motivepick.config

import org.motivepick.security.MotiveAuthenticationSuccessHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.OAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails
import javax.servlet.Filter

@Configuration
class FacebookConfig {

    @Autowired
    private lateinit var oauth2ClientContext: OAuth2ClientContext

    @Autowired
    private lateinit var authenticationSuccessHandler: MotiveAuthenticationSuccessHandler

    @Bean
    @ConfigurationProperties("facebook.client")
    fun facebook(): AuthorizationCodeResourceDetails {
        return AuthorizationCodeResourceDetails()
    }

    @Bean
    @ConfigurationProperties("facebook.resource")
    fun facebookResource(): ResourceServerProperties {
        return ResourceServerProperties()
    }

    @Bean
    fun facebookOAuthFilter(): Filter {
        val facebookFilter = OAuth2ClientAuthenticationProcessingFilter("/oauth2/authorization/facebook")
        val restTemplate = OAuth2RestTemplate(facebook(), oauth2ClientContext)
        val tokenServices = UserInfoTokenServices(facebookResource().userInfoUri, facebook().clientId)
        tokenServices.setRestTemplate(restTemplate)
        facebookFilter.setTokenServices(tokenServices)
        facebookFilter.setRestTemplate(restTemplate)
        facebookFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler)
        return facebookFilter
    }
}
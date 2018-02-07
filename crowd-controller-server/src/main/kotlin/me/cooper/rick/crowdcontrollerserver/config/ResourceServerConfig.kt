package me.cooper.rick.crowdcontrollerserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices

@Configuration
@EnableResourceServer
class ResourceServerConfig(
        private val tokenServices: ResourceServerTokenServices
): ResourceServerConfigurerAdapter() {

    @Value("\${security.jwt.resource-ids}") private val resourceIds: String = ""

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.resourceId(resourceIds)
                .tokenServices(tokenServices)
    }

    override fun configure(http: HttpSecurity) {
        http.requestMatchers()
                .and()
                .authorizeRequests()
                .antMatchers("/actuator/**", "api-docs/**", "user/**")
                .permitAll()
                .antMatchers("/springjwt/**")
                .authenticated()
    }
}
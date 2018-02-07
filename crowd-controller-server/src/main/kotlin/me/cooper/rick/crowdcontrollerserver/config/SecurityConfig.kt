package me.cooper.rick.crowdcontrollerserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.encoding.ShaPasswordEncoder
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
        private val userDetailsService: UserDetailsService
): WebSecurityConfigurerAdapter() {

    @Value("\${security.signing-key}") private val signingKey: String = ""
    @Value("\${security.security-realm}") private val securityRealm: String = ""
    @Value("\${security.encoding-strength}") private val encodingStrength: Int = 256

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder(10)

    @Bean
    fun shaPasswordEncoder(): ShaPasswordEncoder = ShaPasswordEncoder(encodingStrength)

    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter {
        val converter = JwtAccessTokenConverter()
        converter.setSigningKey(signingKey)
        return converter
    }

    @Bean
    fun tokenStore(): TokenStore = JwtTokenStore(accessTokenConverter())

    @Bean
    @Primary //Making this primary to avoid any accidental duplication with another token service instance of the same name
    fun tokenServices(): DefaultTokenServices {
        val defaultTokenServices = DefaultTokenServices()
        defaultTokenServices.setTokenStore(tokenStore())
        defaultTokenServices.setSupportRefreshToken(true)
        return defaultTokenServices
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder())
//                .passwordEncoder(shaPasswordEncoder())
    }

    override fun configure(http: HttpSecurity) {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic()
                .realmName(securityRealm)
                .and()
                .csrf()
                .disable()
    }
}
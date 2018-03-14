package me.cooper.rick.crowdcontrollerserver.config

import me.cooper.rick.crowdcontrollerapi.dto.user.UserDto
import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.TokenEnhancer
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import java.util.*


@Configuration
@EnableAuthorizationServer
internal class AuthorizationServerConfig(
        private val tokenStore: TokenStore,
        private val accessTokenConverter: JwtAccessTokenConverter,
        private val authenticationManager: AuthenticationManager,
        private val userService: UserService
): AuthorizationServerConfigurerAdapter() {

    @Value("\${security.jwt.client-id}") private val clientId: String = ""
    @Value("\${security.jwt.client-secret}") private val clientSecret: String = ""
    @Value("\${security.jwt.grant-types}") private val grantTypes: String = ""
    @Value("\${security.jwt.scope-read}") private val scopeRead: String = ""
    @Value("\${security.jwt.scope-write}") private val scopeWrite: String = "read"
    @Value("\${security.jwt.resource-ids}") private val resourceIds: String = ""

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.inMemory()
                .withClient(clientId)
                .secret(clientSecret)
                .authorizedGrantTypes(*(grantTypes.split(" ").toTypedArray()))
                .scopes(scopeRead, scopeWrite)
                .resourceIds(resourceIds)
                .accessTokenValiditySeconds(-1)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        val enhancerChain = TokenEnhancerChain()
        enhancerChain.setTokenEnhancers(listOf(accessTokenConverter, CustomTokenEnhancer()))

        endpoints.tokenStore(tokenStore)
                .accessTokenConverter(accessTokenConverter)
                .tokenEnhancer(enhancerChain)
                .authenticationManager(authenticationManager)
    }

    inner class CustomTokenEnhancer : TokenEnhancer {

        override fun enhance(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): OAuth2AccessToken {
            val user = authentication.principal as User

            val additionalInfo = HashMap<String, Any>()
            val userDto: UserDto? = userService.user(user.username)
            additionalInfo["user"] = userDto as UserDto

            (accessToken as DefaultOAuth2AccessToken).additionalInformation = additionalInfo

            return accessToken
        }

    }

}

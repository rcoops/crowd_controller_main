package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.LoginDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.lang.String.format

@Service
class SecurityServiceImpl(val authManager: AuthenticationManager,
                          val userDetailsService: UserDetailsService): SecurityService {

    override fun findLoggedInUsername(): String? {
        val userDetails = SecurityContextHolder.getContext().authentication.details
        return if (userDetails is UserDetails) userDetails.username else null
    }

    override fun autoLogin(dto: LoginDto) {
        val userDetails= userDetailsService.loadUserByUsername(dto.username)
        val authToken = UsernamePasswordAuthenticationToken(userDetails, dto.password, userDetails.authorities)

        authManager.authenticate(authToken)

        if (authToken.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = authToken
            logger.debug("Auto login ${dto.username} successfully!")
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
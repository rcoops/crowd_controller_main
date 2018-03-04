package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerserver.controller.error.exception.UserNotFoundException
import me.cooper.rick.crowdcontrollerserver.persistence.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class UserDetailsServiceImpl(private val userRepository: UserRepository): UserDetailsService {

    @Transactional(readOnly = true)
    @Throws(UserNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username) ?: userRepository.findByEmail(username) ?:
                throw UserNotFoundException("User with name $username does not exist")
        val grantedAuthorities = user.roles.map { SimpleGrantedAuthority(it.name) }

        return org.springframework.security.core.userdetails.User(
                user.username,
                user.password,
                grantedAuthorities
        )
    }

}

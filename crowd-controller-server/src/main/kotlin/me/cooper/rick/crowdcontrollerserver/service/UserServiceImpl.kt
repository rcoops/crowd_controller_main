package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.LoginDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.domain.User
import me.cooper.rick.crowdcontrollerserver.repository.RoleRepository
import me.cooper.rick.crowdcontrollerserver.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository,
                      private val roleRepository: RoleRepository,
                      private val bCryptPasswordEncoder: BCryptPasswordEncoder): UserService {

    override fun save(dto: UserDto): UserDto {
        val dtoUser = User.fromDto(dto)
        val user = User(
                username = dtoUser.username,
                password = bCryptPasswordEncoder.encode(dtoUser.password),
                roles = roleRepository.findAll().toSet()
        )
        userRepository.save(user)
        return user.toDto()
    }

    override fun findByUsername(username: String): User {
        return userRepository.findByUsername(username)
    }

}
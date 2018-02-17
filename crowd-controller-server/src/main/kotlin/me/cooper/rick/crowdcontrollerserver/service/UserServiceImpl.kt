package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.domain.User
import me.cooper.rick.crowdcontrollerserver.repository.RoleRepository
import me.cooper.rick.crowdcontrollerserver.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.Principal

@Service
@Transactional
internal class UserServiceImpl(private val userRepository: UserRepository,
                               private val roleRepository: RoleRepository,
                               private val bCryptPasswordEncoder: PasswordEncoder) : UserService {

    override fun create(dto: RegistrationDto): UserDto {
        val user = newUser(dto)
        return userRepository.save(user)
                .toDto()
    }

    override fun allUsers(): List<UserDto> {
        return userRepository.findAll()
                .map(User::toDto)
    }

    override fun user(username: String): UserDto {
        val user = userRepository.findByUsername(username)
        val dto = user!!.toDto()
        return dto
    }

    private fun newUser(dto: RegistrationDto): User {
        val user = User.fromDto(dto)
        return user.copy(
                password = bCryptPasswordEncoder.encode(dto.password),
                roles = user.roles.map { roleRepository.findByName(it.name) }.toSet())
    }

}
package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.domain.User
import me.cooper.rick.crowdcontrollerserver.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.security.Principal

@Service
internal class UserServiceImpl(private val userRepository: UserRepository,
                               private val bCryptPasswordEncoder: BCryptPasswordEncoder) : UserService {

    override fun create(dto: RegistrationDto): UserDto {
        return userRepository.save(newUser(dto))
                .toDto()
    }

    override fun allUsers(): List<UserDto> {
        return userRepository.findAll()
                .map(User::toDto)
    }

    private fun newUser(dto: RegistrationDto): User {
        return User.fromDto(dto)
                .copy(password = bCryptPasswordEncoder.encode(dto.password))
    }

    override fun user(username: String): UserDto {
        return userRepository.findByUsername(username)!!.toDto()
    }

}
package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.domain.User
import java.security.Principal

interface UserService {

    fun create(dto: RegistrationDto): UserDto

    fun allUsers(): List<UserDto>

    fun user(username: String): UserDto

}
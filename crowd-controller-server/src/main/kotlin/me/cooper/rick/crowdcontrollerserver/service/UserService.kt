package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.LoginDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.domain.User

interface UserService {

    fun save(dto: UserDto): UserDto

    fun findByUsername(username: String): User

}
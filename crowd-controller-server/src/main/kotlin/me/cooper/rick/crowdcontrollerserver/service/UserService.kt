package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.domain.User
import java.security.Principal

interface UserService {

    fun create(dto: RegistrationDto): UserDto

    fun allUsers(): List<UserDto>

    fun user(username: String): UserDto?

    fun user(id: Long): UserDto?

    fun friends(id: Long): Set<FriendDto>

    fun addFriend(userId: Long, friendIdentifier: String): UserDto

    fun acceptFriendRequest(userId: Long, friendId: Long): UserDto

}
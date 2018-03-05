package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto

interface UserService {

    fun create(dto: RegistrationDto): UserDto

    fun allUsers(): List<UserDto>

    fun user(username: String): UserDto?

    fun user(id: Long): UserDto?

    fun friends(id: Long): List<FriendDto>

    fun addFriend(userId: Long, friendIdentifier: String): List<FriendDto>

    fun respondToFriendRequest(userId: Long, friendId: Long, isAccepting: Boolean): List<FriendDto>

    fun cancelFriendRequest(userId: Long, friendId: Long): List<FriendDto>

    fun deleteFriend(userId: Long, friendId: Long): List<FriendDto>

}

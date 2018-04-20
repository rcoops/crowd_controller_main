package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.user.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.group.LocationDto
import me.cooper.rick.crowdcontrollerapi.dto.user.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.user.UserDto

interface UserService {

    fun create(dto: RegistrationDto): UserDto

    fun delete(userId: Long)

    fun updateLocation(userId: Long, dto: LocationDto): UserDto

    fun allUsers(): List<UserDto>

    fun user(username: String): UserDto?

    fun user(id: Long): UserDto?

    fun friends(id: Long): List<FriendDto>

    fun addFriend(userId: Long, friendDto: FriendDto): List<FriendDto>

    fun updateFriendship(userId: Long, friendId: Long, friendDto: FriendDto): List<FriendDto>

    fun deleteFriend(userId: Long, friendId: Long): List<FriendDto>

    fun sendGroupInvites()

    fun clearLocationOfUngroupedUsers()

}

package me.cooper.rick.crowdcontrollerapi.dto

data class UserDto(
        val id: Long = -1,
        val username: String = "",
        val email: String = "",
        val friends: Set<FriendDto> = setOf(),
        val roles: Set<String> = setOf("STANDARD_USER")
)
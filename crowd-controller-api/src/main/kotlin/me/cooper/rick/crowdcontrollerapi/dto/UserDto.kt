package me.cooper.rick.crowdcontrollerapi.dto

import me.cooper.rick.crowdcontrollerapi.constants.Role

data class UserDto(
        val id: Long = -1,
        val username: String = "",
        val email: String = "",
        val mobileNumber: String = "",
        val friends: Set<FriendDto> = setOf(),
        val roles: Set<String> = setOf(Role.ROLE_USER.name),
        val group: Long? = null,
        val groupAccepted: Boolean = false
)
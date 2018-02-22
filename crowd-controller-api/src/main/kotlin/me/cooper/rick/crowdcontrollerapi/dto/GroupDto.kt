package me.cooper.rick.crowdcontrollerapi.dto

data class GroupDto(val id: Long = -1,
                    val admin: UserDto = UserDto(),
                    val members: List<UserDto> = emptyList())
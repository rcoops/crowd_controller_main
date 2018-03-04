package me.cooper.rick.crowdcontrollerapi.dto

data class GroupDto(val id: Long = -1,
                    val adminId: Long = -1,
                    val members: List<UserDto> = emptyList())

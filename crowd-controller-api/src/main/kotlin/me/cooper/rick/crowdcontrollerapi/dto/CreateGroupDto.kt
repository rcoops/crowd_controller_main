package me.cooper.rick.crowdcontrollerapi.dto

data class CreateGroupDto(
        val adminId: Long = 0,
        val members: List<Long> = emptyList()
)
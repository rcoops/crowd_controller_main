package me.cooper.rick.crowdcontrollerapi.dto

data class GroupDto(
        val id: Long = 0,
        val adminId: Long = 0,
        val members: Set<Long> = emptySet()
)
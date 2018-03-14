package me.cooper.rick.crowdcontrollerapi.dto.group

data class CreateGroupDto(
        val adminId: Long = 0,
        val members: List<GroupMemberDto> = emptyList()
)

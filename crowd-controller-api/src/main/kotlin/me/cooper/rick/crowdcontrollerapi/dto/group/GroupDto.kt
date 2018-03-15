package me.cooper.rick.crowdcontrollerapi.dto.group

data class GroupDto(val id: Long = -1,
                    val adminId: Long = -1,
                    val members: List<GroupMemberDto> = emptyList(),
                    val location: LocationDto? = null,
                    val settings: GroupSettingsDto = GroupSettingsDto())

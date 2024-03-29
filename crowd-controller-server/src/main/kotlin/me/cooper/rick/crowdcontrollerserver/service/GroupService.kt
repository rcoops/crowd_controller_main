package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.group.CreateGroupDto
import me.cooper.rick.crowdcontrollerapi.dto.group.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.group.GroupSettingsDto
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group

internal interface GroupService {

    fun group(groupId: Long): GroupDto

    fun groups(): List<GroupDto>

    fun create(dto: CreateGroupDto): GroupDto

    fun update(groupId: Long, dto: GroupDto): GroupDto

    fun updateSettings(groupId: Long, dto: GroupSettingsDto): GroupDto

    fun removeFromGroup(groupId: Long, userId: Long): GroupDto?

    fun removeGroup(groupId: Long): Boolean

    fun admin(groupId: Long): String

    fun respondToInvite(groupId: Long, userId: Long, isAccept: Boolean): GroupDto?

    fun isInGroup(groupId: Long, username: String): Boolean

    fun toGroupDto(group: Group): GroupDto

    fun expireGroups()

}

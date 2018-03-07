package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.CreateGroupDto
import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto

interface GroupService {

    fun group(groupId: Long): GroupDto

    fun groups(): List<GroupDto>

    fun create(dto: CreateGroupDto): GroupDto

    fun update(groupId: Long, dto: GroupDto): GroupDto

    fun removeFromGroup(groupId: Long, userId: Long): GroupDto

    fun removeGroup(groupId: Long): Boolean

    fun admin(groupId: Long): String

    fun acceptInvite(groupId: Long, userId: Long): GroupDto

    fun isInGroup(groupId: Long, username: String): Boolean

}

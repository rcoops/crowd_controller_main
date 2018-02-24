package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.CreateGroupDto
import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto

interface GroupService {

    fun group(groupId: Long): GroupDto

    fun groups(): List<GroupDto>

    fun create(dto: CreateGroupDto): GroupDto

    fun addToGroup(groupId: Long, userId: Long): GroupDto

    fun removeFromGroup(groupId: Long, userId: Long): GroupDto

    fun removeGroup(groupId: Long): Boolean

    fun admin(groupId: Long): String

    fun acceptGroupInvite(groupId: Long, userId: Long): GroupDto

}
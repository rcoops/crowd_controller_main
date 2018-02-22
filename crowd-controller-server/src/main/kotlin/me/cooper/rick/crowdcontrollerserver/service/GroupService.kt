package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto

interface GroupService {

    fun group(groupId: Long): GroupDto?

    fun groups(): List<GroupDto>

    fun create(groupDto: GroupDto): UserDto

    fun addToGroup(groupId: Long, userId: Long): GroupDto

    fun removeFromGroup(groupId: Long, userId: Long): GroupDto

    fun removeGroup(groupId: Long): Boolean

}
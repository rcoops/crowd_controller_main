package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto

interface GroupService {

    fun create(userId: Long): UserDto

    fun addToGroup(groupId: Long, userId: Long): GroupDto

    fun removeFromGroup(groupId: Long, userId: Long): GroupDto

}
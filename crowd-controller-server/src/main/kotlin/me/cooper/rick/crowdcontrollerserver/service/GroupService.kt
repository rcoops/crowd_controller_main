package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.UserDto

interface GroupService {

    fun create(userId: Long): UserDto

}
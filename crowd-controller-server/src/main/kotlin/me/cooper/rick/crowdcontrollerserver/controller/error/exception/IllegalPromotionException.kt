package me.cooper.rick.crowdcontrollerserver.controller.error.exception

import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto

class IllegalPromotionException(userDto: UserDto, groupDto: GroupDto)
    : BadHttpRequestException("Cannot promote user: ${userDto.username} to admin as they are not a member of group: " +
        groupDto.id)

package me.cooper.rick.crowdcontrollerserver.controller.error.exception

import me.cooper.rick.crowdcontrollerapi.dto.group.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.user.UserDto

class IllegalPromotionException(userDto: UserDto, groupDto: GroupDto)
    : BadHttpRequestException("Cannot promote user: ${userDto.username} to admin as they are not an accepted member " +
        "of group: ${groupDto.id}")

package me.cooper.rick.crowdcontrollerserver.controller.error.exception

import me.cooper.rick.crowdcontrollerapi.dto.user.UserDto
import me.cooper.rick.crowdcontrollerserver.persistence.model.User

class UserInGroupException(override val message: String = "User already in group"): BadHttpRequestException(message) {

    constructor(userDto: UserDto): this("User ${userDto.username} already belongs to a different group")

    constructor(users: List<UserDto>): this("Users: [${users.mapToName()}] already belong to a group")

    companion object {
        fun List<UserDto>.mapToName() = map(UserDto::username).joinToString(", ")
    }

}

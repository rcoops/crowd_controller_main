package me.cooper.rick.crowdcontrollerserver.controller.error.exception

import me.cooper.rick.crowdcontrollerapi.dto.UserDto

class UserInGroupException(override val message: String = "User already in group"): BadHttpRequestException(message) {

    constructor(userDto: UserDto): this("User ${userDto.username} already belongs to a different group")

    constructor(users: List<UserDto>): this("Users: [${users.joinToString(", ")}] already belong to a group")

}

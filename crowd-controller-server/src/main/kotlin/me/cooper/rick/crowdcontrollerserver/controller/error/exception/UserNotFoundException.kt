package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class UserNotFoundException(message: String = "User not found"): NotFoundException(message) {

    constructor(id: Long): this("User with id: $id not found")

}

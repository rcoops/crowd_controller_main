package me.cooper.rick.crowdcontrollerserver.controller.exception

class UserNotFoundException(override val message: String = "User not found"): ResourceNotFoundException(message) {
    constructor(id: Long): this("User with id: $id not found")
}
package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class UserNotFoundException(identifier: String)
    : NotFoundException("User with identifier: $identifier not found") {

    constructor(id: Long): this("User with id: $id not found")

}

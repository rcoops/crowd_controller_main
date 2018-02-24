package me.cooper.rick.crowdcontrollerserver.controller.exception

class UserNotGroupedException(override val message: String = "User is not invited to group"): UserGroupException(message) {
    constructor(id: Long): this("User with id: $id has not been invited to this group")
}
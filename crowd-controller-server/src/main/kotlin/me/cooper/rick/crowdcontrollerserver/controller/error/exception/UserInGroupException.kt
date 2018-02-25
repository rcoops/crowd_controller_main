package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class UserInGroupException(override val message: String = "User already in group"): UserGroupException(message) {
    constructor(id: Long): this("User with id: $id already belongs to a group")
    constructor(ids: List<Long>): this("Users with ids: [${ids.joinToString(", ")}] already belong to a group")
}
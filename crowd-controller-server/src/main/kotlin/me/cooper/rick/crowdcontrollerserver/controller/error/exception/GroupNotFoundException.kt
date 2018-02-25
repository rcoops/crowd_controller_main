package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class GroupNotFoundException(override val message: String = "Group not found"): ResourceNotFoundException(message) {
    constructor(id: Long): this("Group with id: $id not found")
}
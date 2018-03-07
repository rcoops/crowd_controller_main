package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class GroupNotFoundException(id: Long): NotFoundException("Group with id: $id not found")

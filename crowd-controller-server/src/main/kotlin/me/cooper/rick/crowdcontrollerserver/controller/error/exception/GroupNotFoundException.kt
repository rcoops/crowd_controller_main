package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class GroupNotFoundException(id: Long): ResourceNotFoundException("Group with id: $id not found")


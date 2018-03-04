package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class UserNotGroupedException(id: Long)
    : ResourceExistsException("User with id: $id has not been invited to this group")

package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class UserNotGroupedException(id: Long)
    : BadHttpRequestException("User with id: $id has not been invited to this group")

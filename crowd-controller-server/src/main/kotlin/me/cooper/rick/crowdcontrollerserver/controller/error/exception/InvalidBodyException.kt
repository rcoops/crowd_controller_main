package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class InvalidBodyException(pathId: Long, bodyId: Long?)
    : BadHttpRequestException("Path resource identifier $pathId & body id ${bodyId ?: "non-existent"} do not match")

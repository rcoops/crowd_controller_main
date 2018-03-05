package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class InvalidBodyException(pathId: Long, bodyId: Long)
    : Exception("Path resource identifier $pathId & body id $bodyId do not match")

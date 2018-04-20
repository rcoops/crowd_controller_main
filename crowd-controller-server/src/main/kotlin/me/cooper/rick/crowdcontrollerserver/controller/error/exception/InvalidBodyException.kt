package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class InvalidBodyException(pathIdentifier: String, bodyIdentifier: String?)
    : BadHttpRequestException("Path resource identifier $pathIdentifier & body identifier" +
        " ${bodyIdentifier ?: "non-existent"} do not match") {
    constructor(pathId: Long, bodyId: Long?) : this(pathId.toString(), bodyId?.toString())
}

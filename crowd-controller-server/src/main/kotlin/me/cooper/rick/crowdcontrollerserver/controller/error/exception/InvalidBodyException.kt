package me.cooper.rick.crowdcontrollerserver.controller.error.exception

class InvalidBodyException(message: String) : BadHttpRequestException(message) {
    constructor(pathId: Long, bodyId: Long?) : this(pathId.toString(), bodyId?.toString())
    constructor(pathIdentifier: String, bodyIdentifier: String?)
            : this("Path resource identifier $pathIdentifier & body identifier" +
            " ${bodyIdentifier ?: "non-existent"} do not match")
}

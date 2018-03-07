package me.cooper.rick.crowdcontrollerserver.controller.error.exception

abstract class NotFoundException(override val message: String = "Resource not found"): Exception(message)

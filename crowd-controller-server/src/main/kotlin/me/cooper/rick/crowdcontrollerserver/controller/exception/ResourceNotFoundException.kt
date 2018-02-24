package me.cooper.rick.crowdcontrollerserver.controller.exception

open class ResourceNotFoundException(override val message: String = "Resource not found"): Exception(message)
package me.cooper.rick.crowdcontrollerserver.controller.error.exception

open class ResourceExistsException(override val message: String = "Resource already exists!"): Exception(message)

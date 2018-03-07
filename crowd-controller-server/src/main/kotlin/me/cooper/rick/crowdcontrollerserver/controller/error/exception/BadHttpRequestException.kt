package me.cooper.rick.crowdcontrollerserver.controller.error.exception

abstract class BadHttpRequestException(override val message: String): Exception(message)

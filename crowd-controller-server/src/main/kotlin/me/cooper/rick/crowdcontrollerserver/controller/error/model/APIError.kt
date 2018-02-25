package me.cooper.rick.crowdcontrollerserver.controller.error.model

class APIError(val status: Int = 500,
               val message: String = "Server Error")
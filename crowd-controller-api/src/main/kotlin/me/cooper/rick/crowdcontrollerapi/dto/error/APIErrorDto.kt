package me.cooper.rick.crowdcontrollerapi.dto.error

class APIErrorDto(val status: Int = 503,
                  val message: String = "Connection Error",
                  val detail: String = "Unfortunately the service is not currently available. Please try again later.")
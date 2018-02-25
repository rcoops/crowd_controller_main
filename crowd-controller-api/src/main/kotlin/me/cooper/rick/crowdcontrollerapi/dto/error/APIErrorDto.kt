package me.cooper.rick.crowdcontrollerapi.dto.error

class APIErrorDto(val status: Int = 500,
                  val message: String = "Server Error")
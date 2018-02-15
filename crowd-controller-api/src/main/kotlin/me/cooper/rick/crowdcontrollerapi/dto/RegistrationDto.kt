package me.cooper.rick.crowdcontrollerapi.dto

data class RegistrationDto(
        val username: String = "",
        val password: String,
        val email: String = "",
        val mobileNumber: String
)
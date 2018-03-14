package me.cooper.rick.crowdcontrollerapi.dto.user

data class RegistrationDto(
        var username: String = "",
        var password: String = "",
        var email: String = "",
        var mobileNumber: String = ""
)

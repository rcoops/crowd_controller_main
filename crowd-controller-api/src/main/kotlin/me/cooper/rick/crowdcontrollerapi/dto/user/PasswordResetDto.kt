package me.cooper.rick.crowdcontrollerapi.dto.user

data class PasswordResetDto(val userId: Long = -1L,
                            val oldPassword: String = "",
                            val newPassword: String = "")

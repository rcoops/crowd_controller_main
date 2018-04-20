package me.cooper.rick.crowdcontrollerapi.dto.user

data class PasswordResetDto(val userId: Long,
                            val oldPassword: String,
                            val newPassword: String)

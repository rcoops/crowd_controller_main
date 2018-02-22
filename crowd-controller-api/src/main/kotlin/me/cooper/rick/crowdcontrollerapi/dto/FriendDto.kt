package me.cooper.rick.crowdcontrollerapi.dto

import java.util.*

data class FriendDto(
        val id: Long = 0,
        val username: String = "",
        val isInviter: Boolean = false,
        val activated: Boolean = false
)
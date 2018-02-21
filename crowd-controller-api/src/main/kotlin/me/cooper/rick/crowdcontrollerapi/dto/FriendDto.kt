package me.cooper.rick.crowdcontrollerapi.dto

data class FriendDto(
        val id: Long = 0,
        val username: String = "",
        val isInviter: Boolean = false,
        val activated: Boolean = false
)
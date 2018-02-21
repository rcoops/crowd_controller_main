package me.cooper.rick.crowdcontrollerapi.dto

data class FriendDto(
        val id: Long,
        val username: String,
        val isInviter: Boolean,
        val activated: Boolean
)
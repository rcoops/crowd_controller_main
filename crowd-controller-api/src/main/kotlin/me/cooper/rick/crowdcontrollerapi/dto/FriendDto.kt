package me.cooper.rick.crowdcontrollerapi.dto

import com.fasterxml.jackson.annotation.JsonIgnore

data class FriendDto(
        val id: Long = 0,
        val username: String = "",
        val status: Status = Status.TO_ACCEPT,
        val groupStatus: GroupStatus = GroupStatus.INACTIVE) {

    @JsonIgnore
    fun isGrouped() = GroupStatus.INACTIVE != groupStatus

    @JsonIgnore
    fun canJoinGroup() = !isGrouped() && status == Status.CONFIRMED

    enum class Status {
        CONFIRMED, TO_ACCEPT, AWAITING_ACCEPT
    }

    enum class GroupStatus {
        CONFIRMED, TO_ACCEPT, INACTIVE
    }

    companion object {
        fun getStatus(isInviter: Boolean, isFriendshipActivated: Boolean) = when {
            isFriendshipActivated -> FriendDto.Status.CONFIRMED
            isInviter -> FriendDto.Status.AWAITING_ACCEPT
            else -> FriendDto.Status.TO_ACCEPT
        }
    }

}

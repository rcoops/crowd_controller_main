package me.cooper.rick.crowdcontrollerapi.dto

import com.fasterxml.jackson.annotation.JsonIgnore

data class FriendDto(
        val id: Long = 0,
        val username: String = "",
        val status: Status = Status.INACTIVE,
        val groupStatus: GroupStatus = GroupStatus.INACTIVE) {

    @JsonIgnore
    fun isGrouped() = GroupStatus.INACTIVE != groupStatus

    @JsonIgnore
    fun canJoinGroup() = !isGrouped() && status == Status.CONFIRMED

    enum class Status {
        CONFIRMED, TO_ACCEPT, AWAITING_ACCEPT, INACTIVE
    }

    enum class GroupStatus {
        CONFIRMED, TO_ACCEPT, INACTIVE
    }

    companion object {
        fun getFriendStatus(isInviter: Boolean, isFriendshipActivated: Boolean) = when {
            isFriendshipActivated -> FriendDto.Status.CONFIRMED
            isInviter -> FriendDto.Status.TO_ACCEPT
            else -> FriendDto.Status.AWAITING_ACCEPT
        }
        fun getGroupStatus(hasGroup: Boolean, hasAccepted: Boolean): FriendDto.GroupStatus {
            return when {
                !hasGroup -> FriendDto.GroupStatus.INACTIVE
                hasAccepted -> FriendDto.GroupStatus.CONFIRMED
                else -> FriendDto.GroupStatus.TO_ACCEPT
            }
        }
    }

}

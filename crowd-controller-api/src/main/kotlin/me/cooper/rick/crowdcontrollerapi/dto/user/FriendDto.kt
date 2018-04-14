package me.cooper.rick.crowdcontrollerapi.dto.user

import com.fasterxml.jackson.annotation.JsonIgnore

data class FriendDto(
        val id: Long = 0,
        val username: String = "",
        val status: Status = Status.INACTIVE,
        val groupStatus: GroupStatus = GroupStatus.INACTIVE) {

    @JsonIgnore
    fun isGrouped() = GroupStatus.CONFIRMED == groupStatus

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
            isFriendshipActivated -> Status.CONFIRMED
            isInviter -> Status.TO_ACCEPT
            else -> Status.AWAITING_ACCEPT
        }
        fun getGroupStatus(hasGroup: Boolean, hasAccepted: Boolean): GroupStatus {
            return when {
                !hasGroup -> GroupStatus.INACTIVE
                hasAccepted -> GroupStatus.CONFIRMED
                else -> GroupStatus.TO_ACCEPT
            }
        }
    }

}

package me.cooper.rick.crowdcontrollerapi.dto

data class FriendDto(
        val id: Long = 0,
        val username: String = "",
        val status: Status = Status.TO_ACCEPT,
        val inGroup: Boolean = false) {

    enum class Status {
        ACTIVATED, AWAITING_ACCEPT, TO_ACCEPT
    }

    companion object {
        fun getStatus(isInviter: Boolean, isFriendshipActivated: Boolean) = when {
            isFriendshipActivated -> FriendDto.Status.ACTIVATED
            isInviter -> FriendDto.Status.AWAITING_ACCEPT
            else -> FriendDto.Status.TO_ACCEPT
        }
    }

}
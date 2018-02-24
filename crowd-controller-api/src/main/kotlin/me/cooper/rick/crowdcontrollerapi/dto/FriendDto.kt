package me.cooper.rick.crowdcontrollerapi.dto

data class FriendDto(
        val id: Long = 0,
        val username: String = "",
        val inGroup: Boolean = false,
        private val isInviter: Boolean = false,
        private val activated: Boolean = false) {

    val status: Status = when {
        activated -> Status.ACTIVATED
        isInviter -> Status.AWAITING_ACCEPT
        else -> Status.TO_ACCEPT
    }

    enum class Status {
        ACTIVATED, AWAITING_ACCEPT, TO_ACCEPT
    }

}
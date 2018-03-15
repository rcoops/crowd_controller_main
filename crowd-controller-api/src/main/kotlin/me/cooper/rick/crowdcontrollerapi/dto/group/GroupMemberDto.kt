package me.cooper.rick.crowdcontrollerapi.dto.group

import me.cooper.rick.crowdcontrollerapi.dto.user.FriendDto

data class GroupMemberDto(val id: Long = 0,
                          val username: String = "",
                          val groupAccepted: Boolean = false) {

    companion object {
        fun fromFriendDto(dto: FriendDto) = GroupMemberDto(dto.id, dto.username)
    }

}

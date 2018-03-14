package me.cooper.rick.crowdcontrollerapi.dto.group

data class GroupMemberDto(val id: Long = 0,
                          val username: String = "",
                          val groupAccepted: Boolean = false)

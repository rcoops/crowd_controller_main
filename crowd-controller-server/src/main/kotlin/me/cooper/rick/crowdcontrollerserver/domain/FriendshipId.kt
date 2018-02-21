package me.cooper.rick.crowdcontrollerserver.domain

import java.io.Serializable

internal data class FriendshipId(var inviter: Long? = null, var invitee: Long? = null): Serializable
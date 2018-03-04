package me.cooper.rick.crowdcontrollerserver.persistence.model

import java.io.Serializable

internal data class FriendshipId(var inviter: Long? = null, var invitee: Long? = null): Serializable

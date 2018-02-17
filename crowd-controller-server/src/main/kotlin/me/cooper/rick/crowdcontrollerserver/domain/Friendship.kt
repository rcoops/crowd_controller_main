package me.cooper.rick.crowdcontrollerserver.domain

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name="friendship")
internal data class Friendship(
        @Id @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "inviter_id") val inviter: User? = null,
        @Id @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "invitee_id") val invitee: User? = null,
        val activated: Boolean = false): Serializable  {

    fun isInviter(username: String): Boolean = inviter?.username == username

    fun partner(username: String): User? = if (isInviter(username)) invitee else inviter

}
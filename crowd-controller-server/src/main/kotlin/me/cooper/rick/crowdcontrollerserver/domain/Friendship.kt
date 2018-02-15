package me.cooper.rick.crowdcontrollerserver.domain

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name="friendship")
internal class Friendship(
        @Id @ManyToOne @JoinColumn(name = "inviter_id") val inviter: User,
        @Id @ManyToOne @JoinColumn(name = "invitee_id") val invitee: User,
        val activated: Boolean): Serializable  {

    fun isInviter(username: String): Boolean = inviter.username == username

    fun partner(username: String): User = if (isInviter(username)) invitee else inviter

}
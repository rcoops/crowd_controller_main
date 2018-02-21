package me.cooper.rick.crowdcontrollerserver.domain

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@IdClass(FriendshipId::class)
@Table(name="friendship")
internal data class Friendship(
        @Id @ManyToOne @JoinColumn(name = "inviter_id") var inviter: User? = null,
        @Id @ManyToOne @JoinColumn(name = "invitee_id") var invitee: User? = null,
        var activated: Boolean = false): Serializable {

    fun isInviter(username: String): Boolean = inviter?.username == username

    fun partner(username: String): User? = if (isInviter(username)) invitee else inviter

    // Overrides required to prevent circular reference

    override fun hashCode(): Int {
        return Objects.hash(inviter?.id, invitee?.id, activated)
    }

    override fun toString(): String {
        return "Friendship(inviter_id=${inviter?.id}, invitee_id=${invitee?.id}, activated=$activated"
    }

}
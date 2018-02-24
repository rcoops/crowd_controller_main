package me.cooper.rick.crowdcontrollerserver.domain

import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name = "clique")
internal data class Group(
        @Id @GeneratedValue(strategy = AUTO) val id: Long? = null,
        @OneToOne val admin: User? = null,
        @OneToMany(mappedBy = "group") val members: MutableSet<User> = mutableSetOf(),
        val created: Timestamp = Timestamp.valueOf(LocalDateTime.now())) {

    fun toDto(): GroupDto = GroupDto(id!!, admin!!.toDto(), members.map { it.toDto() })

    private fun memberIds(): Array<Long> {
        return members.map { it.id }.toTypedArray()
    }

    override fun hashCode(): Int {
        return Objects.hash(id, admin?.id, *memberIds(), created)
    }

    override fun toString(): String {
        return "Group(id=$id, admin_id=${admin?.id}, created= $created" +
                "members=[${memberIds().joinToString(", ")}])"
    }

    companion object {

        fun fromUsers(user: User, members: List<User>): Group {
            return Group(null, user, members.toMutableSet())
        }

    }

}
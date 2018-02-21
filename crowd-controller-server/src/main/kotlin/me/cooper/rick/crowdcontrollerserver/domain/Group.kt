package me.cooper.rick.crowdcontrollerserver.domain

import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import java.util.*
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name = "clique")
internal data class Group(
        @Id @GeneratedValue(strategy = AUTO) val id: Long = -1,
        @OneToOne val admin: User? = null,
        @OneToMany(mappedBy = "group") val members: MutableSet<User> = mutableSetOf()) {

    fun toDto(): GroupDto = GroupDto(id, admin?.id ?: -1, memberIds().toSet())

    private fun memberIds(): Array<Long> {
        return members.map { it.id }.toTypedArray()
    }

    override fun hashCode(): Int {
        return Objects.hash(id, admin?.id, *memberIds())
    }

    override fun toString(): String {
        return "Group(id=$id, admin_id=${admin?.id}, " +
                "members=[${memberIds().joinToString(", ")}])"
    }

}
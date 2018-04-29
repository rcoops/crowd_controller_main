package me.cooper.rick.crowdcontrollerserver.persistence.model

import me.cooper.rick.crowdcontrollerapi.dto.group.GroupSettingsDto
import me.cooper.rick.crowdcontrollerserver.persistence.listeners.GroupListener
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import java.util.Objects.hash
import javax.persistence.*
import javax.persistence.CascadeType.MERGE
import javax.persistence.CascadeType.PERSIST
import javax.persistence.GenerationType.AUTO
import javax.persistence.TemporalType.TIMESTAMP

@Entity
@EntityListeners(GroupListener::class, AuditingEntityListener::class)
@Table(name = "`group`")
internal data class Group(
        @Id @GeneratedValue(strategy = AUTO)
        val id: Long? = null,

        @OneToOne
        val admin: User? = null,

        @OneToMany(mappedBy = "group", cascade = [MERGE, PERSIST])
        val members: MutableSet<User> = mutableSetOf(),

        val created: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
        val expiry: Timestamp = Timestamp.valueOf(LocalDateTime.now().plusHours(12)),

        @LastModifiedDate
        @Temporal(TIMESTAMP)
        val lastModified: Date? = null,

        @OneToOne(cascade = [CascadeType.ALL], optional = false)
        @JoinColumn(name = "settings_id", referencedColumnName = "id")
        val settings: GroupSettings = GroupSettings()) {

    fun settingsFromDto(dto: GroupSettingsDto?) = settings.fromDto(dto)

    fun acceptedMembers() = members.filter { it.groupAccepted }

    fun hasMoreThanOneAcceptedMember() = members.count { it.groupAccepted } > 1

    private fun memberIds(): Array<Long> {
        return members.map(User::id).toTypedArray()
    }

    override fun hashCode(): Int {
        return hash(id, admin?.id, *memberIds(), created, lastModified, settings)
    }

    override fun toString(): String {
        return "Group(id=$id, admin_id=${admin?.id}, created=$created, " +
                "members=[${memberIds()}])"
    }

    companion object {

        fun fromUsers(user: User, members: List<User>): Group {
            return Group(null, user, members.toMutableSet())
        }

    }

}

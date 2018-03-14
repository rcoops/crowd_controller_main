package me.cooper.rick.crowdcontrollerserver.persistence.model

import me.cooper.rick.crowdcontrollerapi.dto.group.GroupDto
import me.cooper.rick.crowdcontrollerserver.persistence.listeners.GroupListener
import me.cooper.rick.crowdcontrollerserver.persistence.location.LocationResolver
import me.cooper.rick.crowdcontrollerserver.persistence.location.MultiLocationResolver
import me.cooper.rick.crowdcontrollerserver.persistence.location.SingleLocationResolver
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.persistence.TemporalType.TIMESTAMP
import javax.persistence.GenerationType.AUTO

@Entity
@EntityListeners(GroupListener::class, AuditingEntityListener::class)
@Table(name = "`group`")
internal data class Group(
        @Id @GeneratedValue(strategy = AUTO) val id: Long? = null,
        @OneToOne var admin: User? = null,
        @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL]) val members: MutableSet<User> = mutableSetOf(),
        val created: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
        @LastModifiedDate @Temporal(TIMESTAMP) val lastModified: Date? = null,
        @OneToOne
        @JoinColumn(name = "settings_id", referencedColumnName = "id")
        val settings: GroupSettings = GroupSettings(),
        @Transient private var resolver: LocationResolver = resolver(settings.isClustering)) {

    fun toDto(): GroupDto {
        return GroupDto(id!!, admin!!.id, members.map { it.toGroupMemberDto() }, resolver.location(this),
                settings.toDto())
    }

    private fun memberIds(): Array<Long> {
        return members.map { it.id }.toTypedArray()
    }

    override fun hashCode(): Int {
        return Objects.hash(id, admin?.id, *memberIds(), created)
    }

    override fun toString(): String {
        return "Group(id=$id, admin_id=${admin?.id}, created=$created, " +
                "members=[${memberIds().joinToString(", ")}])"
    }

    companion object {

        fun fromUsers(user: User, members: List<User>): Group {
            return Group(null, user, members.toMutableSet())
        }

        private fun resolver(isClustering: Boolean): LocationResolver {
            return if (isClustering) MultiLocationResolver() else SingleLocationResolver()
        }

    }

}

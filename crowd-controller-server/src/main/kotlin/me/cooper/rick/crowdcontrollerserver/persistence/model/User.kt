package me.cooper.rick.crowdcontrollerserver.persistence.model

import me.cooper.rick.crowdcontrollerapi.dto.user.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.user.FriendDto.Companion.getFriendStatus
import me.cooper.rick.crowdcontrollerapi.dto.user.FriendDto.Companion.getGroupStatus
import me.cooper.rick.crowdcontrollerapi.dto.group.GroupMemberDto
import me.cooper.rick.crowdcontrollerapi.dto.user.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.user.UserDto
import me.cooper.rick.crowdcontrollerserver.controller.error.handler.RestResponseExceptionHandler.Companion.UNIQUE_EMAIL
import me.cooper.rick.crowdcontrollerserver.controller.error.handler.RestResponseExceptionHandler.Companion.UNIQUE_MOBILE
import me.cooper.rick.crowdcontrollerserver.controller.error.handler.RestResponseExceptionHandler.Companion.UNIQUE_USERNAME
import me.cooper.rick.crowdcontrollerserver.persistence.listeners.UserListener
import java.sql.Timestamp
import java.util.*
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@EntityListeners(UserListener::class)
@Table(uniqueConstraints = [
    UniqueConstraint(name = UNIQUE_USERNAME, columnNames = ["username"]),
    UniqueConstraint(name = UNIQUE_EMAIL, columnNames = ["email"])
])
internal data class User(
        @Id @GeneratedValue(strategy = AUTO)
        val id: Long = 0,

        val username: String = "",

        var password: String = "",

        @Column(nullable = false)
        val email: String = "",

        val mobileNumber: String = "",

        @ManyToMany
        @JoinTable(name = "user_role",
                joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
                inverseJoinColumns = [(JoinColumn(name="role_id", referencedColumnName = "id"))])
        var roles: Set<Role> = setOf(Role()),

        @ManyToOne @JoinColumn(name="group_id", referencedColumnName = "id")
        val group: Group? = null,

        val groupAccepted: Boolean = false,

        val latitude: Double? = null,

        val longitude: Double? = null,

        val lastLocationUpdate: Timestamp? = null,

        val passwordResetToken: String? = null) {

    fun toDto(): UserDto {
        return UserDto(
                id,
                username,
                email,
                mobileNumber,
                roles = roles.map { it.name }.toSet(),
                group = group?.id,
                groupAdmin = group?.admin?.username,
                groupAccepted = groupAccepted
        )
    }

    fun toGroupMemberDto(): GroupMemberDto {
        return GroupMemberDto(
                id,
                username,
                groupAccepted
        )
    }

    internal fun hasLocation() = latitude != null && longitude != null

    override fun hashCode(): Int = Objects.hash(id, username, email, password, mobileNumber)


    fun toFriendDto(friendship: Friendship): FriendDto {
        return FriendDto(
                id =  id,
                username = username,
                status = getFriendStatus(!friendship.isInviter(username), friendship.activated),
                groupStatus = getGroupStatus(group != null, groupAccepted)
        )
    }

    companion object {

        fun fromDto(dto: RegistrationDto): User {
            return User(
                    username = dto.username,
                    password = dto.password,
                    email = dto.email,
                    mobileNumber = dto.mobileNumber
            )
        }

    }

}

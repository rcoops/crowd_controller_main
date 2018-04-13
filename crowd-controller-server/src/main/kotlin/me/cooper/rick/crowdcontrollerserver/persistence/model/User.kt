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
import java.util.*
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@EntityListeners(UserListener::class)
@Table(uniqueConstraints = [
    UniqueConstraint(name = UNIQUE_USERNAME, columnNames = ["username"]),
    UniqueConstraint(name = UNIQUE_EMAIL, columnNames = ["email"]),
    UniqueConstraint(name = UNIQUE_MOBILE, columnNames = ["mobileNumber"])
])
internal data class User(
        @Id @GeneratedValue(strategy = AUTO)
        val id: Long = 0,

        val username: String = "",

        var password: String = "",

        val email: String = "",

        @Column(nullable = false)
        val mobileNumber: String = "",

        @ManyToMany
        @JoinTable(name = "user_role",
                joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
                inverseJoinColumns = [(JoinColumn(name="role_id", referencedColumnName = "id"))])
        var roles: Set<Role> = setOf(Role()),

        @OneToMany(mappedBy = "inviter", cascade = [CascadeType.ALL])
        private val friendsInviters: Set<Friendship> = emptySet(),

        @OneToMany(mappedBy = "invitee", cascade = [CascadeType.ALL])
        private val friendsInvitees: Set<Friendship> = emptySet(),

        @ManyToOne @JoinColumn(name="group_id", referencedColumnName = "id")
        val group: Group? = null,

        val groupAccepted: Boolean = false,

        val latitude: Double? = null,

        val longitude: Double? = null) {


    fun toDto(): UserDto {
        return UserDto(
                id,
                username,
                email,
                mobileNumber,
                friendsToDto(),
                roles.map { it.name }.toSet(),
                group?.id,
                group?.admin?.username,
                groupAccepted
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

    private fun friends(): Set<Friendship> = (friendsInviters + friendsInvitees).toSet()

    private fun friendsToDto(): List<FriendDto> {
        return friends().map(::toFriendDto)
                .sortedWith(compareBy(FriendDto::status, FriendDto::username))
    }

    private fun toFriendDto(friendship: Friendship): FriendDto {
        val partner = friendship.partner(username)
        return FriendDto(
                id = partner?.id ?: -1,
                username = partner?.username ?: "",
                status = getFriendStatus(!friendship.isInviter(username), friendship.activated),
                groupStatus = getGroupStatus(partner?.group != null, partner?.groupAccepted ?: false)
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

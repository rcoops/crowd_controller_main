package me.cooper.rick.crowdcontrollerserver.persistence.model

import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.FriendDto.Companion.getStatus
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.controller.error.handler.RestResponseExceptionHandler.Companion.UNIQUE_EMAIL
import me.cooper.rick.crowdcontrollerserver.controller.error.handler.RestResponseExceptionHandler.Companion.UNIQUE_MOBILE
import me.cooper.rick.crowdcontrollerserver.controller.error.handler.RestResponseExceptionHandler.Companion.UNIQUE_USERNAME
import java.util.*
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name="user", uniqueConstraints = [
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

        @OneToMany(mappedBy = "inviter")
        private val friendsInviters: Set<Friendship> = emptySet(),

        @OneToMany(mappedBy = "invitee")
        private val friendsInvitees: Set<Friendship> = emptySet(),

        @ManyToOne @JoinColumn(name="group_id", referencedColumnName = "id")
        val group: Group? = null,

        val groupAccepted: Boolean = false) {

    fun toDto(): UserDto {
        return UserDto(
                id,
                username,
                email,
                mobileNumber,
                friendsToDto(),
                roles.map { it.name }.toSet(),
                group?.id,
                groupAccepted
        )
    }

    override fun hashCode(): Int = Objects.hash(id, username, email, password, mobileNumber)

    private fun friends(): Set<Friendship> = (friendsInviters + friendsInvitees).toSet()

    private fun friendsToDto(): List<FriendDto> {
        return friends().map(this::toFriendDto)
                .sortedWith(compareBy(FriendDto::status, FriendDto::username))
    }

    private fun toFriendDto(friendship: Friendship): FriendDto {
        fun getGroupStatus(user: User?): FriendDto.GroupStatus {
            return when {
                user?.group == null -> FriendDto.GroupStatus.INACTIVE
                user.groupAccepted -> FriendDto.GroupStatus.CONFIRMED
                else -> FriendDto.GroupStatus.TO_ACCEPT
            }
        }
        val partner = friendship.partner(username)
        return FriendDto(
                id = partner?.id ?: -1,
                username = partner?.username ?: "",
                status = getStatus(!friendship.isInviter(username), friendship.activated),
                groupStatus = getGroupStatus(partner)
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

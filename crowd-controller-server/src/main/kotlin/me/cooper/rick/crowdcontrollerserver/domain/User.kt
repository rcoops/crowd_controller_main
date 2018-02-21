package me.cooper.rick.crowdcontrollerserver.domain

import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import java.util.*
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name="user")
internal data class User(
        @Id @GeneratedValue(strategy = AUTO)
        val id: Long = 0,

        @Column(unique = true)
        val username: String = "",

        var password: String = "",

        @Column(unique = true)
        val email: String = "",

        @Column(unique = true, nullable = false)
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
        private val group: Group? = null) {

    fun toDto(): UserDto {
        return UserDto(
                id,
                username,
                email,
                mobileNumber,
                friends().map(this::toFriendDto).toSet(),
                roles.map { it.name }.toSet(),
                group?.id
        )
    }

    override fun hashCode(): Int = Objects.hash(id, username, email, password, mobileNumber)

    private fun friends() = (friendsInviters + friendsInvitees).toSet()

    private fun toFriendDto(friendship: Friendship): FriendDto {
        val partner = friendship.partner(username)
        return FriendDto(
                partner?.id ?: -1,
                partner?.username ?: "",
                !friendship.isInviter(username),
                friendship.activated
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
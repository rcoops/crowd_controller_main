package me.cooper.rick.crowdcontrollerserver.domain

import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name="users")
internal data class User(
        @Id @GeneratedValue(strategy = AUTO) private val id: Long = 0,

        @Column(unique = true) val username: String = "",

        var password: String = "",

        @Column(unique = true) val email: String = "",

        @Column(unique = true, nullable = false) val mobileNumber: String = "",

        @ManyToMany
        @JoinTable(name = "user_role",
                joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
                inverseJoinColumns = [(JoinColumn(name="role_id", referencedColumnName = "id"))])
        val roles: Set<Role> = setOf(Role(name = Role.USER)),

        @OneToMany(mappedBy = "inviter")
        private val friendsInviters: Set<Friendship> = emptySet(),

        @OneToMany(mappedBy = "invitee")
        private val friendsInvitees: Set<Friendship> = emptySet()) {

    fun toDto(): UserDto {
        return UserDto(
                id,
                username,
                email,
                (friendsInvitees + friendsInviters).map(this::toFriendDto).toSet(),
                roles.map { it.name }.toSet()
        )
    }

    private fun toFriendDto(friendship: Friendship): FriendDto {
        return FriendDto(
                friendship.partner(username)?.username ?: "",
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
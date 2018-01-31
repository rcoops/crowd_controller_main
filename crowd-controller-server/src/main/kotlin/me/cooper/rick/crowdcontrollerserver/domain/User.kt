package me.cooper.rick.crowdcontrollerserver.domain

import me.cooper.rick.crowdcontrollerapi.dto.LoginDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name="users")
data class User(
        @Id @GeneratedValue(strategy = AUTO) val id: Long = 0,
        val username: String = "",
        val password: String = "",
        @ManyToMany
        @JoinTable(name = "user_role",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [(JoinColumn(name="role_id"))])
        val roles: Set<Role> = setOf()) {

    fun toDto(): UserDto {
        return UserDto(username, password)
    }

    companion object {
        fun fromDto(dto: UserDto): User {
            return User(username = dto.username, password = dto.password)
        }
    }
}
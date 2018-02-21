package me.cooper.rick.crowdcontrollerserver.domain

import me.cooper.rick.crowdcontrollerapi.constants.Role
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name = "role")
internal data class Role(
        @Id @GeneratedValue(strategy = AUTO) val id: Long = 0,
        val name: String = Role.ROLE_USER.name
)
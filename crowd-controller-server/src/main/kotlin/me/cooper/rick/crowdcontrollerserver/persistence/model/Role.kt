package me.cooper.rick.crowdcontrollerserver.persistence.model

import me.cooper.rick.crowdcontrollerapi.constants.Role
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.AUTO
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "role")
internal data class Role(
        @Id @GeneratedValue(strategy = AUTO) val id: Long = 0,
        val name: String = Role.ROLE_USER.name
) {
    override fun toString(): String {
        return name
    }
}
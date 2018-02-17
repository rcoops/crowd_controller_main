package me.cooper.rick.crowdcontrollerserver.domain

import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name = "role")
internal data class Role(
        @Id @GeneratedValue(strategy = AUTO) val id: Long = 0,
        val name: String = ""
) {
    companion object {
        const val USER = "ROLE_USER"
        const val ADMIN = "ROLE_ADMIN"
    }
}
package me.cooper.rick.crowdcontrollerserver.domain

import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name = "roles")
data class Role(
        @Id @GeneratedValue(strategy = AUTO) val id: Long = 0,
        val name: String = ""
)
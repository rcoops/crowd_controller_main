package me.cooper.rick.crowdcontrollerserver.domain

import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name = "clique")
internal data class Group(
        @Id @GeneratedValue(strategy = AUTO) val id: Long = -1,
        @OneToOne val admin: User? = null,
        @OneToMany(mappedBy = "group") val members: Set<User> = emptySet())
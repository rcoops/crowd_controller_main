package me.cooper.rick.crowdcontrollerserver.repository

import me.cooper.rick.crowdcontrollerserver.domain.Role
import org.springframework.data.jpa.repository.JpaRepository

internal interface RoleRepository: JpaRepository<Role, Long> {

    fun findAllByNameIn(name: Collection<String>): List<Role>

}
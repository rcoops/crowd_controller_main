package me.cooper.rick.crowdcontrollerserver.persistence.repository

import me.cooper.rick.crowdcontrollerserver.persistence.model.Role
import org.springframework.data.jpa.repository.JpaRepository

internal interface RoleRepository: JpaRepository<Role, Long> {

    fun findAllByNameIn(name: Collection<String>): List<Role>

}

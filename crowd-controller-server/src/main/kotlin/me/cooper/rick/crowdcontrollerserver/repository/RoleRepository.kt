package me.cooper.rick.crowdcontrollerserver.repository

import me.cooper.rick.crowdcontrollerserver.domain.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository: JpaRepository<Role, Long>
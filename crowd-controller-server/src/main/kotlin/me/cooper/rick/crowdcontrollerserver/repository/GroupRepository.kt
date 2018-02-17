package me.cooper.rick.crowdcontrollerserver.repository

import me.cooper.rick.crowdcontrollerserver.domain.Group
import org.springframework.data.jpa.repository.JpaRepository

internal interface GroupRepository: JpaRepository<Group, Long>
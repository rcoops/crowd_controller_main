package me.cooper.rick.crowdcontrollerserver.persistence.repository

import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import org.springframework.data.jpa.repository.JpaRepository

internal interface GroupRepository: JpaRepository<Group, Long>

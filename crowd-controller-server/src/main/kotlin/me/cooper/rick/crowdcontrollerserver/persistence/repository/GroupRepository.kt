package me.cooper.rick.crowdcontrollerserver.persistence.repository

import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.*

internal interface GroupRepository: JpaRepository<Group, Long> {

    fun findByExpiryBefore(date: Date): List<Group>

}

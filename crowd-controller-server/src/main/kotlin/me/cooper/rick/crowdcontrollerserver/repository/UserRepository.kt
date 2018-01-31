package me.cooper.rick.crowdcontrollerserver.repository

import me.cooper.rick.crowdcontrollerserver.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun findByUsername(username: String?): User
}
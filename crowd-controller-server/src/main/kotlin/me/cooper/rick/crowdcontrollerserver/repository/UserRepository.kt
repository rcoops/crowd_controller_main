package me.cooper.rick.crowdcontrollerserver.repository

import me.cooper.rick.crowdcontrollerserver.domain.Group
import me.cooper.rick.crowdcontrollerserver.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

internal interface UserRepository: JpaRepository<User, Long> {

    fun findByUsername(username: String?): User?

    fun findByEmail(email: String?): User?

    @Query("FROM User WHERE email = :identifier OR username = :identifier OR mobile_number = :identifier")
    fun findFirstByEmailOrUsernameOrMobileNumber(@Param("identifier") identifier: String?): User?

    fun findByGroup(group: Group): List<User>

}
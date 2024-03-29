package me.cooper.rick.crowdcontrollerserver.persistence.repository

import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

internal interface UserRepository: JpaRepository<User, Long> {

    fun findAllByIdIn(ids: Set<Long>): List<User>

    fun findByUsername(username: String?): User?

    fun findByEmail(email: String?): User?

    fun findByEmailAndPasswordResetToken(email: String?, token: String?): User?

    @Query("FROM User WHERE email = :identifier OR username = :identifier OR mobile_number = :identifier")
    fun findByEmailOrUsernameOrMobileNumber(@Param("identifier") identifier: String?): User?

    fun findByGroup(group: Group): List<User>

    @Query("FROM User WHERE group != NULL AND groupAccepted = false")
    fun findAllWithPendingInvites(): List<User>

    @Query("FROM User WHERE group = NULL AND (latitude != NULL OR longitude != NULL)")
    fun findAllUnGroupedWithLocation(): List<User>

}

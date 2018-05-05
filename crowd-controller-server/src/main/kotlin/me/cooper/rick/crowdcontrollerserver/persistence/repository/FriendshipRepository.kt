package me.cooper.rick.crowdcontrollerserver.persistence.repository

import me.cooper.rick.crowdcontrollerserver.persistence.model.Friendship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

internal interface FriendshipRepository: JpaRepository<Friendship, Long> {

    @Query("FROM Friendship WHERE inviter_id = :idOne AND invitee_id = :idTwo OR invitee_id = :idOne AND inviter_id = :idTwo")
    fun findFriendshipBetweenUsers(@Param("idOne") userOneId: Long,
                                   @Param("idTwo") userTwoId: Long): Friendship?

    @Query("FROM Friendship WHERE inviter_id = :id or invitee_id = :id")
    fun findByUserId(@Param("id") id: Long): List<Friendship>

}

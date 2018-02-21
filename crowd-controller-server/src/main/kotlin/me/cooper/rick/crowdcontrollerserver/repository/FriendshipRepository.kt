package me.cooper.rick.crowdcontrollerserver.repository

import me.cooper.rick.crowdcontrollerserver.domain.Friendship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

internal interface FriendshipRepository: JpaRepository<Friendship, Long> {

    @Query("FROM Friendship WHERE inviter_id = :idOne AND invitee_id = :idTwo OR invitee_id = :idOne AND inviter_id = :idTwo")
    fun findFriendshipBetweenUsers(@Param("idOne") userOneId: Long,
                                   @Param("idTwo") userTwoId: Long): Friendship?

}
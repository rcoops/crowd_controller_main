package me.cooper.rick.crowdcontrollerserver.persistence.listeners

import me.cooper.rick.crowdcontrollerserver.persistence.model.Friendship
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.persistence.PostPersist

class FriendshipListener {

    @PostPersist
    internal fun postFriendshipUpdate(friendship: Friendship) {
        LOG.debug(friendship.toString())
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(FriendshipListener::class.java)
    }

}

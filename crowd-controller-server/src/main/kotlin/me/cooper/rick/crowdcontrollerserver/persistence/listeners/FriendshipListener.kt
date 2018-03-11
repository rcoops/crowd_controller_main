package me.cooper.rick.crowdcontrollerserver.persistence.listeners

import me.cooper.rick.crowdcontrollerserver.AutowireHelper
import me.cooper.rick.crowdcontrollerserver.controller.WebsocketController
import me.cooper.rick.crowdcontrollerserver.persistence.model.Friendship
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.persistence.PostPersist
import javax.persistence.PostUpdate

@Component
class FriendshipListener {

    @Autowired
    private var controller: WebsocketController? = null

    @PostPersist
    @PostUpdate
    internal fun postFriendshipUpdate(friendship: Friendship) {
        AutowireHelper.autowire(this)
        controller?.send(friendship.invitee!!.toDto(), friendship.inviter!!.toDto())
        LOG.debug(friendship.toString())
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(FriendshipListener::class.java)
    }

}

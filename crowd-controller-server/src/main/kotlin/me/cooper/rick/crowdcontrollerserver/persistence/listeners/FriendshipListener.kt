package me.cooper.rick.crowdcontrollerserver.persistence.listeners

import me.cooper.rick.crowdcontrollerserver.AutowireHelper
import me.cooper.rick.crowdcontrollerserver.controller.WebSocketController
import me.cooper.rick.crowdcontrollerserver.persistence.model.Friendship
import me.cooper.rick.crowdcontrollerserver.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.persistence.PostPersist
import javax.persistence.PostUpdate

@Component
class FriendshipListener {

    @Autowired
    private var controller: WebSocketController? = null

    @Autowired
    private var userService: UserService? = null

    @PostPersist
    @PostUpdate
    internal fun postFriendshipUpdate(friendship: Friendship) {
        AutowireHelper.autowire(this)
        userService?.user(friendship.invitee!!.id)?.let { controller?.send(it) }
        userService?.user(friendship.inviter!!.id)?.let { controller?.send(it) }
        LOG.debug(friendship.toString())
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(FriendshipListener::class.java)
    }

}

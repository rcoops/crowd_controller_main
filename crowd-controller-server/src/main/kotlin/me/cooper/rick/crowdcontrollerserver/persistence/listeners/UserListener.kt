package me.cooper.rick.crowdcontrollerserver.persistence.listeners

import me.cooper.rick.crowdcontrollerserver.AutowireHelper
import me.cooper.rick.crowdcontrollerserver.controller.WebsocketController
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.persistence.PostPersist
import javax.persistence.PostUpdate

@Component
class UserListener {

    @Autowired
    private var controller: WebsocketController? = null

    @PostPersist
    @PostUpdate
    internal fun postUser(user: User) {
        AutowireHelper.autowire(this)
        controller?.send(user.toDto())
        sendGroupLocationUpdate(user)
        LOG.debug(user.toString())
    }

    private fun sendGroupLocationUpdate(user: User) {
        user.group?.let { if (it.isClustering || it.admin == user) controller?.send(it.toDto()) }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(UserListener::class.java)
    }

}

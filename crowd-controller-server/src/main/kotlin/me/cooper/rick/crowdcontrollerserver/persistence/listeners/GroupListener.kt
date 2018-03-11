package me.cooper.rick.crowdcontrollerserver.persistence.listeners

import me.cooper.rick.crowdcontrollerserver.AutowireHelper
import me.cooper.rick.crowdcontrollerserver.controller.WebsocketController
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.persistence.PostPersist

@Component
class GroupListener {

    @Autowired private var controller: WebsocketController? = null

    @PostPersist
    internal fun postUser(group: Group) {
        AutowireHelper.autowire(this)
        controller?.greeting(group.toDto())
        LOG.debug(group.toString())
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(GroupListener::class.java)
    }

}

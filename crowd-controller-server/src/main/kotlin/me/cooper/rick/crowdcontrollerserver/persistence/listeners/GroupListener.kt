package me.cooper.rick.crowdcontrollerserver.persistence.listeners

import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.persistence.PostPersist

class GroupListener {

    @PostPersist
    internal fun postUser(group: Group) {
        LOG.debug(group.toString())
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(GroupListener::class.java)
    }

}

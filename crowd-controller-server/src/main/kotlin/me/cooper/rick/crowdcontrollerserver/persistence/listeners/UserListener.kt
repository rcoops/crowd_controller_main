package me.cooper.rick.crowdcontrollerserver.persistence.listeners

import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.persistence.PostPersist

class UserListener {

    @PostPersist
    internal fun postUser(user: User) {
        LOG.debug(user.toString())
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(UserListener::class.java)
    }

}

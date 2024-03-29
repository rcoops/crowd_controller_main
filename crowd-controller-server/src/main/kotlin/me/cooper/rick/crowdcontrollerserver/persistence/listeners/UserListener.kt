package me.cooper.rick.crowdcontrollerserver.persistence.listeners

import me.cooper.rick.crowdcontrollerserver.util.AutowireHelper
import me.cooper.rick.crowdcontrollerserver.controller.WebSocketController
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import me.cooper.rick.crowdcontrollerserver.service.GroupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.persistence.PostPersist
import javax.persistence.PostUpdate

@Component
class UserListener {

    @Autowired
    private var controller: WebSocketController? = null

    @Autowired
    private var groupService: GroupService? = null

    private val userGroupCache = mutableMapOf<Long, Long?>()

    @PostPersist
    internal fun create(user: User) {
        AutowireHelper.autowire(this)
        sendUser(user, "Creating")
    }

    @PostUpdate
    internal fun update(user: User) {
        AutowireHelper.autowire(this)
        sendUser(user, "Updating")
        updateGroup(user)
        userGroupCache[user.id] = user.group?.id
        sendGroupLocationUpdate(user)
    }

    private fun updateGroup(user: User) {
        val oldGroupId = userGroupCache[user.id]
        val newGroupId = user.group?.id
        if (oldGroupId != newGroupId) {
            oldGroupId?.let { findGroupAndPost(it) }
            newGroupId?.let { findGroupAndPost(it) }
        }
    }

    private fun findGroupAndPost(groupId: Long?) {
        groupService?.group(groupId!!)?.let { controller?.send(it) }
    }

    private fun sendUser(user: User, action: String) {
        controller?.send(user.toDto())
        LOG.debug("$action $user")
    }

    private fun sendGroupLocationUpdate(user: User) {
        user.group?.let {
            if (it.settings.isClustering || it.admin == user) groupService?.toGroupDto(it)?.let { controller?.send(it) }
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(UserListener::class.java)
    }

}

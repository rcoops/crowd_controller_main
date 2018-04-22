package me.cooper.rick.crowdcontrollerserver.testutil

import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.User

internal fun buildTestGroup(groupId: Long, adminId: Long): Group {
    return Group(groupId, User(adminId, groupAccepted = true))
}

internal fun buildTestUserList(numberOfUsers: Int, startingId: Long = 0, group: Group? = null): List<User> {
    return (startingId until numberOfUsers + startingId).map {
        buildTestUser(it + 1, "User ${it + 1}", group = group)
    }
}

internal fun buildTestUser(id: Long, username: String, group: Group? = null): User {
    return User(id, username, group = group)
}

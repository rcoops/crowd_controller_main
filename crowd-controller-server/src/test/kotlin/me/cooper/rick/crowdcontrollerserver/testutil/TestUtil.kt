package me.cooper.rick.crowdcontrollerserver.testutil

import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.Role
import me.cooper.rick.crowdcontrollerserver.persistence.model.User

internal fun buildTestGroup(groupId: Long, adminId: Long): Group {
    return Group(groupId, User(adminId, groupAccepted = true))
}

internal fun buildTestUserList(numberOfUsers: Int, startingId: Long = 0, role: Role, group: Group? = null): List<User> {
    return (startingId until numberOfUsers + startingId).map {
        buildTestUser(it + 1, "User ${it + 1}", role, group)
    }
}

internal fun buildTestUser(id: Long, username: String, role: Role, group: Group? = null): User {
    return buildIntegrationTestUser(username, role, group).copy(id = id)
}

internal fun buildIntegrationTestUsers(vararg usernames: String, role: Role? = null, group: Group? = null): List<User> {
    return usernames.map { buildIntegrationTestUser(it, role, group) }
}

fun mapToName(start: Int, end: Int) = (start..end).map { it.toString() }.toTypedArray()

internal fun buildIntegrationTestUser(username: String, role: Role? = null, group: Group? = null): User {
    return User(
            username = username,
            group = group,
            email = "$username@email.com",
            mobileNumber = username,
            roles = if (role != null) mutableSetOf(role) else mutableSetOf()
    )
}

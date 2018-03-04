package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.CreateGroupDto
import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.GroupNotFoundException
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.UserInGroupException
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.UserNotFoundException
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.UserNotGroupedException
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import me.cooper.rick.crowdcontrollerserver.persistence.repository.GroupRepository
import me.cooper.rick.crowdcontrollerserver.persistence.repository.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
internal class GroupServiceImpl(private val userRepository: UserRepository,
                                private val groupRepository: GroupRepository): GroupService {

    override fun groups(): List<GroupDto> = groupRepository.findAll().map(Group::toDto)

    override fun group(groupId: Long): GroupDto {
        return groupRepository.findOne(groupId)?.toDto() ?: throw GroupNotFoundException(groupId)
    }

    @Throws(UserInGroupException::class)
    override fun create(dto: CreateGroupDto): GroupDto {
        val user = userRepository.findOne(dto.adminId) ?: throw UserNotFoundException(dto.adminId)
        if (user.group != null) throw UserInGroupException(dto.adminId)

        // Ensure that it doesn't matter if admin id is included in members or not
        val members = userRepository.findAllWithIdIn((dto.members + dto.adminId).toSet())
        val groupedMembers = members.filter { it.group != null }
        if (groupedMembers.isNotEmpty()) throw UserInGroupException(groupedMembers.map(User::id))

        val group = groupRepository.save(Group.fromUsers(user, members))

        groupUsers(group, false, members)
        userRepository.saveAndFlush(user.copy(groupAccepted = true))

        return group.toDto()
    }

    @Throws(UserInGroupException::class, UserNotFoundException::class, GroupNotFoundException::class)
    override fun addToGroup(groupId: Long, userId: Long): GroupDto {
        val user = userRepository.findOne(userId) ?: throw UserNotFoundException(userId)
        if (user.group != null) throw UserInGroupException(userId)
        val group = groupRepository.findOne(groupId) ?: throw GroupNotFoundException(groupId)

        group.members.add(user)
        groupRepository.save(group)

        groupUsers(group, false, user)

        return group.toDto()
    }

    @Throws(UserNotFoundException::class, GroupNotFoundException::class)
    override fun removeFromGroup(groupId: Long, userId: Long): GroupDto {
        val user = userRepository.findOne(userId) ?: throw UserNotFoundException(userId)
        val group = groupRepository.findOne(groupId) ?: throw GroupNotFoundException(groupId)

        group.members.remove(user)
        groupRepository.save(group)

        unGroupUsers(user)

        return group.toDto()
    }

    override fun removeGroup(groupId: Long): Boolean {
        val group = groupRepository.findOne(groupId) ?: throw GroupNotFoundException(groupId)

        val users = userRepository.findByGroup(group)
        unGroupUsers(users)

        groupRepository.delete(groupId)
        groupRepository.flush()

        return true
    }

    @Throws(UsernameNotFoundException::class, GroupNotFoundException::class, UserInGroupException::class)
    override fun acceptGroupInvite(groupId: Long, userId: Long): GroupDto {
        val user = userRepository.findOne(userId) ?: throw UserNotFoundException(userId)
        val group = groupRepository.findOne(groupId) ?: throw GroupNotFoundException(groupId)

        if (user.group == null) throw UserNotGroupedException(userId)
        if (group != user.group) throw UserInGroupException(userId)

        userRepository.saveAndFlush(user.copy(groupAccepted = true))

        return group.toDto()
    }

    @Throws(GroupNotFoundException::class)
    override fun admin(groupId: Long): String {
        val group = groupRepository.findOne(groupId) ?: throw GroupNotFoundException(groupId)
        return group.admin!!.username
    }

    override fun isInGroup(groupId: Long, username: String): Boolean {
        val user = userRepository.findByUsername(username) ?:
        throw UserNotFoundException("User with detail: $username does not exist")
        val group = groupRepository.findOne(groupId) ?: throw GroupNotFoundException(groupId)

        return group.members.any { it.id == user.id }
    }

    @Throws(UserInGroupException::class)
    private fun groupUsers(group: Group, accepted: Boolean, users: List<User>) {
        return groupUsers(group, accepted, *users.toTypedArray())
    }

    @Throws(UserInGroupException::class)
    private fun groupUsers(group: Group, accepted: Boolean, vararg users: User) {
        users.forEach {
            if (it.group != null) throw UserInGroupException(it.id)
            userRepository.save(it.copy(group = group, groupAccepted = accepted))
        }
        userRepository.flush()
    }

    private fun unGroupUsers(users: List<User>) = unGroupUsers(*users.toTypedArray())

    private fun unGroupUsers(vararg users: User) {
        users.forEach { userRepository.save(it.copy(group = null, groupAccepted = false)) }
        userRepository.flush()
    }

}

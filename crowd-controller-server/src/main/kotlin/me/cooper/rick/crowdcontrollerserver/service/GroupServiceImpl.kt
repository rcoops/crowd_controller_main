package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.constants.Role
import me.cooper.rick.crowdcontrollerapi.dto.group.CreateGroupDto
import me.cooper.rick.crowdcontrollerapi.dto.group.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.group.GroupMemberDto
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.*
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import me.cooper.rick.crowdcontrollerserver.persistence.repository.GroupRepository
import me.cooper.rick.crowdcontrollerserver.persistence.repository.RoleRepository
import me.cooper.rick.crowdcontrollerserver.persistence.repository.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
internal class GroupServiceImpl(private val userRepository: UserRepository,
                                private val roleRepository: RoleRepository,
                                private val groupRepository: GroupRepository): GroupService {

    override fun groups(): List<GroupDto> = groupRepository.findAll().map(Group::toDto)

    override fun group(groupId: Long): GroupDto {
        return groupRepository.findOne(groupId)?.toDto() ?: throw GroupNotFoundException(groupId)
    }

    @Throws(UserInGroupException::class)
    override fun create(dto: CreateGroupDto): GroupDto {
        val admin = userRepository.findOne(dto.adminId) ?: throw UserNotFoundException(dto.adminId)
        if (admin.group != null) throw UserInGroupException(admin.toDto())

        // Ensure that it doesn't matter if admin id is included in members or not
        val members = userRepository.findAllWithIdIn((dto.members.map(GroupMemberDto::id) + dto.adminId).toSet())
        val groupedMembers = members.filter { it.group != null }
        if (groupedMembers.isNotEmpty()) throw UserInGroupException(groupedMembers.map(User::toDto))

        val group = groupRepository.save(Group.fromUsers(admin, members))
        groupUsers(group, members)
        userRepository.saveAndFlush(admin.copy(groupAccepted = true, roles = admin.roles + groupAdminRole()))

        return group.toDto()
    }

    @Throws(InvalidBodyException::class, GroupNotFoundException::class, UserNotFoundException::class,
            IllegalPromotionException::class, UserInGroupException::class)
    override fun update(groupId: Long, dto: GroupDto): GroupDto {
        if (groupId != dto.id) throw InvalidBodyException(groupId, dto.id)

        val group = groupRepository.findOne(dto.id) ?: throw GroupNotFoundException(dto.id)

        val admin = userRepository.findOne(dto.adminId) ?: throw UserNotFoundException(dto.adminId)
        if (admin.group != group) throw IllegalPromotionException(admin.toDto(), group.toDto())

        if (group.admin != admin) swapAdminRole(group, admin)

        val newMembers = userRepository.findAllWithIdIn(dto.members.map(GroupMemberDto::id).toSet())
        val membersToRemove = (group.members - newMembers)
        val membersToAdd = (newMembers - group.members)

        groupRepository.saveAndFlush(group.copy(
                admin = admin,
                members = newMembers.toMutableSet(),
                settings = group.settingsFromDto(dto))
        )
        groupUsers(group, *membersToAdd.toTypedArray())
        unGroupUsers(*membersToRemove.toTypedArray())

        return group.toDto()
    }

    @Throws(UserNotFoundException::class, GroupNotFoundException::class)
    override fun removeFromGroup(groupId: Long, userId: Long): GroupDto? {
        val user = userRepository.findOne(userId) ?: throw UserNotFoundException(userId)
        val group = groupRepository.findOne(groupId) ?: throw GroupNotFoundException(groupId)

        if (group.members.filter { it.groupAccepted }.size == 1) {
            removeGroup(groupId)
            return null
        }

        val admin = if (user == group.admin) {
            val nextUser =  group.members.first { it != user && it.groupAccepted }
            group.admin = nextUser
            userRepository.save(nextUser.copy(roles = nextUser.roles + groupAdminRole()))

            nextUser
        } else {
            group.admin
        }

        group.members.remove(user)
        groupRepository.save(if (admin == group.admin) group else group.copy(admin = admin))

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
    override fun acceptInvite(groupId: Long, userId: Long): GroupDto {
        val user = userRepository.findOne(userId) ?: throw UserNotFoundException(userId)
        val group = groupRepository.findOne(groupId) ?: throw GroupNotFoundException(groupId)

        if (user.group == null) throw UserNotGroupedException(userId)
        if (group != user.group) throw UserInGroupException(user.toDto())

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

    private fun swapAdminRole(group: Group, newAdmin: User) {
        val groupAdminRole = groupAdminRole()
        userRepository.save(group.admin!!.copy(roles = group.admin!!.roles - groupAdminRole))
        userRepository.save(newAdmin.copy(roles = newAdmin.roles + groupAdminRole))
    }

    @Throws(UserInGroupException::class)
    private fun groupUsers(group: Group, users: List<User>) = groupUsers(group, *users.toTypedArray())

    @Throws(UserInGroupException::class)
    private fun groupUsers(group: Group, vararg users: User) {
        users.forEach {
            if (it.group != null && it.group.id != group.id) throw UserInGroupException(it.toDto())
            userRepository.save(it.copy(group = group, groupAccepted = it.groupAccepted))
        }
        userRepository.flush()
    }

    private fun unGroupUsers(users: List<User>) = unGroupUsers(*users.toTypedArray())

    private fun unGroupUsers(vararg users: User) {
        val groupAdminRole = groupAdminRole()
        users.forEach {
            userRepository.save(it.copy(group = null, groupAccepted = false, roles = it.roles - groupAdminRole))
        }
        userRepository.flush()
    }

    private fun groupAdminRole() = roleRepository.findAllByNameIn(listOf(Role.ROLE_GROUP_ADMIN.name)).first()

}

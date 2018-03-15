package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.constants.Role
import me.cooper.rick.crowdcontrollerapi.dto.group.CreateGroupDto
import me.cooper.rick.crowdcontrollerapi.dto.group.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.group.GroupMemberDto
import me.cooper.rick.crowdcontrollerapi.dto.group.GroupSettingsDto
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

    @Throws(GroupNotFoundException::class)
    override fun group(groupId: Long): GroupDto = groupEntity(groupId).toDto()

    @Throws(UserInGroupException::class)
    override fun create(dto: CreateGroupDto): GroupDto {
        val admin = userEntity(dto.adminId)
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

        val group = groupEntity(dto.id)

        val admin = userEntity(dto.adminId)
        if (admin.group != group || !admin.groupAccepted) throw IllegalPromotionException(admin.toDto(), group.toDto())

        if (group.admin != admin) swapAdminRole(group, admin)

        val newMembers = userRepository.findAllWithIdIn(dto.members.map(GroupMemberDto::id).toSet())
        val membersToRemove = (group.members - newMembers)
        val membersToAdd = (newMembers - group.members)

        groupRepository.saveAndFlush(group.copy(
                admin = admin,
                members = newMembers.toSet(),
                settings = group.settingsFromDto(dto.settings))
        )
        groupUsers(group, *membersToAdd.toTypedArray())
        unGroupUsers(*membersToRemove.toTypedArray())

        return group.toDto()
    }

    @Throws(GroupNotFoundException::class)
    override fun updateSettings(groupId: Long, dto: GroupSettingsDto): GroupDto {
        val group = groupEntity(groupId)
        return groupRepository.saveAndFlush(group.copy(settings = group.settingsFromDto(dto)))
                .toDto()
    }

    @Throws(UserNotFoundException::class, GroupNotFoundException::class)
    override fun removeFromGroup(groupId: Long, userId: Long): GroupDto? {
        val user = userEntity(userId)
        val group = groupEntity(groupId)

        if (!group.hasMoreThanOneAcceptedMember() && user.groupAccepted) {
            removeGroup(groupId)
            return null
        }
        val newAdmin = getNewAdmin(user, group)
        groupRepository.saveAndFlush(group.copy(
                members = (group.members - userRepository.save(user.copy(
                        group = null,
                        groupAccepted = false,
                        roles = user.roles - groupAdminRole()))),
                admin = userRepository.save(newAdmin.copy(roles = newAdmin.roles + groupAdminRole()))
        ))

        return group.toDto()
    }

    override fun removeGroup(groupId: Long): Boolean {
        val group = groupEntity(groupId)

        val users = userRepository.findByGroup(group)
        unGroupUsers(users)

        groupRepository.delete(groupId)
        groupRepository.flush()

        return true
    }

    @Throws(UsernameNotFoundException::class, GroupNotFoundException::class, UserInGroupException::class)
    override fun acceptInvite(groupId: Long, userId: Long): GroupDto {
        val user = userEntity(userId)
        val group = groupEntity(groupId)

        if (user.group == null) throw UserNotGroupedException(userId)
        if (group != user.group) throw UserInGroupException(user.toDto())

        userRepository.saveAndFlush(user.copy(groupAccepted = true))

        return group.toDto()
    }

    @Throws(GroupNotFoundException::class)
    override fun admin(groupId: Long): String {
        val group = groupEntity(groupId)
        return group.admin!!.username
    }

    override fun isInGroup(groupId: Long, username: String): Boolean {
        val user = userRepository.findByUsername(username) ?:
        throw UserNotFoundException("User with detail: $username does not exist")
        val group = groupEntity(groupId)

        return group.members.any { it.id == user.id }
    }

    private fun groupEntity(groupId: Long): Group {
        return groupRepository.findOne(groupId) ?: throw GroupNotFoundException(groupId)
    }

    private fun getNewAdmin(user: User, group: Group): User {
        return (if (user == group.admin) group.acceptedMembers().find { it != user } else group.admin)!!
    }

    private fun swapAdminRole(group: Group, newAdmin: User) {
        val groupAdminRole = groupAdminRole()
        userRepository.save(group.admin!!.copy(roles = group.admin.roles - groupAdminRole))
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

    @Throws(UserNotFoundException::class)
    private fun userEntity(id: Long) = userRepository.findOne(id) ?: throw UserNotFoundException(id)

}

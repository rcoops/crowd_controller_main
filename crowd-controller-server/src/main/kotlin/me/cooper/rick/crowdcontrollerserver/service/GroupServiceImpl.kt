package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.constants.Role.ROLE_GROUP_ADMIN
import me.cooper.rick.crowdcontrollerapi.dto.CreateGroupDto
import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.domain.Group
import me.cooper.rick.crowdcontrollerserver.domain.User
import me.cooper.rick.crowdcontrollerserver.repository.GroupRepository
import me.cooper.rick.crowdcontrollerserver.repository.RoleRepository
import me.cooper.rick.crowdcontrollerserver.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
internal class GroupServiceImpl(private val userRepository: UserRepository,
                                private val groupRepository: GroupRepository,
                                private val roleRepository: RoleRepository): GroupService {

    override fun groups(): List<GroupDto> = groupRepository.findAll().map { it.toDto() }

    override fun group(groupId: Long): GroupDto? = groupRepository.findOne(groupId).toDto()

    override fun create(createGroupDto: CreateGroupDto): GroupDto {
        val user = userRepository.findOne(createGroupDto.adminId)
        val members = userRepository.findAllWithIdIn(createGroupDto.members)
        val group = groupRepository.save(Group.fromUsers(user, members))
        userRepository.saveAndFlush(user.copy(group = group))
        val groupDto = group.toDto()
        LOGGER.debug("New group created $groupDto by ${user.username}")
        return groupDto
    }

    override fun addToGroup(groupId: Long, userId: Long): GroupDto {
        val user = userRepository.findOne(userId)
        val group = groupRepository.findOne(groupId)
        group.members.add(user)
        groupRepository.save(group)
        userRepository.saveAndFlush(user.copy(group = group))
        return groupRepository.findOne(groupId).toDto()
    }

    override fun removeFromGroup(groupId: Long, userId: Long): GroupDto {
        val user = userRepository.findOne(userId)
        val group = groupRepository.findOne(groupId)
        group.members.remove(user)
        groupRepository.save(group)
        removeGroupFromUsers(listOf(user))
        return groupRepository.findOne(groupId).toDto()
    }

    override fun removeGroup(groupId: Long): Boolean {
        val group = groupRepository.findOne(groupId)
        return if (group == null) {
            LOGGER.debug("Group not found")
            false
        } else {
            val users = userRepository.findByGroup(group)
            removeGroupFromUsers(users)
            groupRepository.delete(groupId)
            groupRepository.flush()
            true
        }
    }

    override fun admin(groupId: Long): String {
        val group = groupRepository.findOne(groupId)
        return group.admin!!.username
    }

    private fun removeGroupFromUsers(users: List<User>) {
        users.forEach { userRepository.save(it.copy(group = null)) }
        userRepository.flush()
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(GroupServiceImpl::class.java)
    }

}
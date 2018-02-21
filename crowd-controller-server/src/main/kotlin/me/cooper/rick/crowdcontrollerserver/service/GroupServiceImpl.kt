package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.constants.Role.ROLE_GROUP_ADMIN
import me.cooper.rick.crowdcontrollerapi.dto.GroupDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.domain.Group
import me.cooper.rick.crowdcontrollerserver.domain.User
import me.cooper.rick.crowdcontrollerserver.repository.GroupRepository
import me.cooper.rick.crowdcontrollerserver.repository.RoleRepository
import me.cooper.rick.crowdcontrollerserver.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
internal class GroupServiceImpl(private val userRepository: UserRepository,
                                private val groupRepository: GroupRepository,
                                private val roleRepository: RoleRepository): GroupService {

    override fun create(userId: Long): UserDto {
        val user = userRepository.findOne(userId)
        val group = groupRepository.save(Group(admin = user, members = mutableSetOf(user)))
        val role = roleRepository.findByName(ROLE_GROUP_ADMIN.name)
        userRepository.saveAndFlush(user.copy(group = group, roles = (user.roles + role)))
        return userRepository.findOne(userId).toDto()
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
        userRepository.saveAndFlush(user.copy(group = null))
        return groupRepository.findOne(groupId).toDto()
    }

}
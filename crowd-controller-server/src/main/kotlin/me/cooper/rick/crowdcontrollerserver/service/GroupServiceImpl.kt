package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.domain.Group
import me.cooper.rick.crowdcontrollerserver.repository.GroupRepository
import me.cooper.rick.crowdcontrollerserver.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
internal class GroupServiceImpl(private val userRepository: UserRepository,
                                private val groupRepository: GroupRepository): GroupService {

    override fun create(userId: Long): UserDto {
        val user = userRepository.findOne(userId)
        val group = Group(admin = user, members = setOf(user))
        groupRepository.saveAndFlush(group)
        val newUser = userRepository.findOne(userId)
        val newGroup = groupRepository.findAll()
        return newUser.toDto()
    }
}
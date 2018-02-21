package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.domain.Friendship
import me.cooper.rick.crowdcontrollerserver.domain.User
import me.cooper.rick.crowdcontrollerserver.repository.FriendshipRepository
import me.cooper.rick.crowdcontrollerserver.repository.RoleRepository
import me.cooper.rick.crowdcontrollerserver.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
internal class UserServiceImpl(private val userRepository: UserRepository,
                               private val roleRepository: RoleRepository,
                               private val friendshipRepository: FriendshipRepository,
                               private val bCryptPasswordEncoder: PasswordEncoder) : UserService {

    override fun create(dto: RegistrationDto): UserDto {
        val user = newUser(dto)
        return userRepository.save(user)
                .toDto()
    }

    override fun allUsers(): List<UserDto> {
        return userRepository.findAll()
                .map(User::toDto)
    }

    override fun user(username: String): UserDto? = userRepository.findByUsername(username)?.toDto()

    override fun user(id: Long): UserDto? = userRepository.findOne(id)?.toDto()

    override fun friends(id: Long): Set<FriendDto> = userRepository.findOne(id).toDto().friends

    override fun addFriend(userId: Long, friendIdentifier: String): UserDto {
        val user = userRepository.findOne(userId)
        val friend = userRepository.findFirstByEmailOrUsernameOrMobileNumber(friendIdentifier)
        if (!isExistingFriendship(userId, friend!!.id)) {
            val friendship = Friendship(user, friend, false)
            friendshipRepository.saveAndFlush(friendship)
        }
        val newUSer = userRepository.findOne(userId)
        val newDto = newUSer.toDto()
        return newDto
    }

    override fun acceptFriendRequest(userId: Long, friendId: Long): UserDto {
        val friendship = friendshipRepository.findFriendshipBetweenUsers(userId, friendId)
        friendshipRepository.save(friendship?.copy(activated = true))

        return userRepository.findOne(userId).toDto()
    }

    private fun newUser(dto: RegistrationDto): User {
        val user = User.fromDto(dto)
        return user.copy(
                password = bCryptPasswordEncoder.encode(dto.password),
                roles = user.roles.map { roleRepository.findByName(it.name) }.toSet())
    }

    private fun isExistingFriendship(userId: Long, friendId: Long): Boolean {
        return friendshipRepository.findFriendshipBetweenUsers(userId, friendId) != null
    }

}
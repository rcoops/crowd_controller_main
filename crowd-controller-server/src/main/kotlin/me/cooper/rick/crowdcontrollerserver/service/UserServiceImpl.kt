package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.controller.exception.UserNotFoundException
import me.cooper.rick.crowdcontrollerserver.domain.Friendship
import me.cooper.rick.crowdcontrollerserver.domain.Role
import me.cooper.rick.crowdcontrollerserver.domain.User
import me.cooper.rick.crowdcontrollerserver.repository.FriendshipRepository
import me.cooper.rick.crowdcontrollerserver.repository.RoleRepository
import me.cooper.rick.crowdcontrollerserver.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
internal class UserServiceImpl(private val userRepository: UserRepository,
                               private val roleRepository: RoleRepository,
                               private val friendshipRepository: FriendshipRepository,
                               private val bCryptPasswordEncoder: PasswordEncoder) : UserService {

    override fun create(dto: RegistrationDto): UserDto = userRepository.save(newUser(dto)).toDto()

    override fun allUsers(): List<UserDto> = userRepository.findAll().map(User::toDto)

    override fun user(username: String): UserDto? = userRepository.findByUsername(username)?.toDto()

    override fun user(id: Long): UserDto? = userRepository.findOne(id)?.toDto()

    override fun friends(id: Long): Set<FriendDto> = userRepository.findOne(id).toDto().friends

    @Throws(UserNotFoundException::class)
    override fun addFriend(userId: Long, friendIdentifier: String): Set<FriendDto> {
        val user = userRepository.findOne(userId)

        val friend = userRepository.findFirstByEmailOrUsernameOrMobileNumber(friendIdentifier) ?:
        throw UserNotFoundException()

        if (!friendshipExists(userId, friend.id)) saveFriendship(Friendship(user, friend, false))

        return userRepository.findOne(userId).toDto().friends
    }

    override fun acceptFriendRequest(userId: Long, friendId: Long): Set<FriendDto> {
        val friendship = friendshipRepository.findFriendshipBetweenUsers(userId, friendId)
        saveFriendship(friendship?.copy(activated = true)!!)

        return userRepository.findOne(userId).toDto().friends
    }

    override fun deleteFriend(userId: Long, friendId: Long): Set<FriendDto> {
        val friendship = friendshipRepository.findFriendshipBetweenUsers(userId, friendId)

        friendship?.let(this::deleteFriendship)

        return userRepository.findOne(userId).toDto().friends
    }

    private fun newUser(dto: RegistrationDto): User {
        val user = User.fromDto(dto)
        val roles = roleRepository.findAllByNameIn(user.roles.map(Role::name)).toSet()

        return user.copy(password = bCryptPasswordEncoder.encode(dto.password), roles = roles)
    }

    private fun friendshipExists(userId: Long, friendId: Long): Boolean {
        return friendshipRepository.findFriendshipBetweenUsers(userId, friendId) != null
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // Needs separate transaction to update user
    fun saveFriendship(friendship: Friendship) {
        friendshipRepository.saveAndFlush(friendship)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // Needs separate transaction to update user
    fun deleteFriendship(friendship: Friendship) {
        friendshipRepository.delete(friendship)
        friendshipRepository.flush()
    }

}

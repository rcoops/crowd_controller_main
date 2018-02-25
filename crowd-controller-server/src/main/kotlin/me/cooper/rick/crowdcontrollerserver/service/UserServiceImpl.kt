package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.UserNotFoundException
import me.cooper.rick.crowdcontrollerserver.persistence.model.Friendship
import me.cooper.rick.crowdcontrollerserver.persistence.model.Role
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import me.cooper.rick.crowdcontrollerserver.persistence.repository.FriendshipRepository
import me.cooper.rick.crowdcontrollerserver.persistence.repository.RoleRepository
import me.cooper.rick.crowdcontrollerserver.persistence.repository.UserRepository
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

    @Throws(UserNotFoundException::class)
    override fun user(username: String): UserDto {
        return userRepository.findByUsername(username)?.toDto() ?:
        throw UserNotFoundException("User with name $username does not exist")
    }

    @Throws(UserNotFoundException::class)
    override fun user(id: Long): UserDto = userEntity(id).toDto()

    @Throws(UserNotFoundException::class)
    override fun friends(id: Long): Set<FriendDto> = user(id).friends

    @Throws(UserNotFoundException::class)
    override fun addFriend(userId: Long, friendIdentifier: String): Set<FriendDto> {
        val user = userEntity(userId)

        val friend = userRepository.findFirstByEmailOrUsernameOrMobileNumber(friendIdentifier) ?:
        throw UserNotFoundException("User with detail: $friendIdentifier does not exist")

        if (!friendshipExists(userId, friend.id)) saveFriendship(Friendship(user, friend, false))

        return friends(userId)
    }

    override fun acceptFriendRequest(userId: Long, friendId: Long): Set<FriendDto> {
        val friendship = friendshipRepository.findFriendshipBetweenUsers(userId, friendId)

        saveFriendship(friendship?.copy(activated = true)!!)

        return friends(userId)
    }

    override fun deleteFriend(userId: Long, friendId: Long): Set<FriendDto> {
        val friendship = friendshipRepository.findFriendshipBetweenUsers(userId, friendId)

        friendship?.let(this::deleteFriendship)

        return friends(userId)
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

    private fun newUser(dto: RegistrationDto): User {
        val user = User.fromDto(dto)
        val roles = roleRepository.findAllByNameIn(user.roles.map(Role::name)).toSet()

        return user.copy(password = bCryptPasswordEncoder.encode(dto.password), roles = roles)
    }

    private fun friendshipExists(userId: Long, friendId: Long): Boolean {
        return friendshipRepository.findFriendshipBetweenUsers(userId, friendId) != null
    }

    @Throws(UserNotFoundException::class)
    private fun userEntity(id: Long): User = userRepository.findOne(id) ?: throw UserNotFoundException(id)

}

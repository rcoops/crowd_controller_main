package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.UserDto
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.FriendshipExistsException
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.FriendshipNotFoundException
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
        return userRepository.findByUsername(username)?.toDto()
                ?: throw UserNotFoundException("User with name $username does not exist")
    }

    @Throws(UserNotFoundException::class)
    override fun user(id: Long): UserDto = userEntity(id).toDto()

    @Throws(UserNotFoundException::class)
    override fun friends(id: Long): List<FriendDto> = user(id).friends

    @Throws(UserNotFoundException::class, FriendshipExistsException::class)
    override fun addFriend(userId: Long, friendIdentifier: String): List<FriendDto> {
        val user = userEntity(userId)

        val friend = userRepository.findFirstByEmailOrUsernameOrMobileNumber(friendIdentifier)
                ?: throw UserNotFoundException("User with detail: $friendIdentifier does not exist")

        if (friendshipExists(userId, friend.id)) throw FriendshipExistsException(friend.username)

        saveFriendship(Friendship(user, friend, false))

        return friends(userId)
    }

    @Throws(UserNotFoundException::class, FriendshipNotFoundException::class)
    override fun respondToFriendRequest(userId: Long, friendId: Long, isAccepting: Boolean): List<FriendDto> {
        val friendship = findFriendship(userId, friendId)

        if (isAccepting) saveFriendship(friendship.copy(activated = true))
        else deleteFriendship(friendship)

        return friends(userId)
    }

    @Throws(UserNotFoundException::class, FriendshipNotFoundException::class, FriendshipExistsException::class)
    override fun cancelFriendRequest(userId: Long, friendId: Long): List<FriendDto> {
        val friendship = findFriendship(userId, friendId)

        if (friendship.activated) throw FriendshipExistsException(userEntity(friendId).username)

        deleteFriendship(friendship)

        return friends(userId)
    }

    @Throws(UserNotFoundException::class, FriendshipNotFoundException::class, FriendshipExistsException::class)
    override fun deleteFriend(userId: Long, friendId: Long): List<FriendDto> {
        deleteFriendship(findFriendship(userId, friendId))

        return friends(userId)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // Needs separate transaction to update user
    fun saveFriendship(friendship: Friendship?) {
        friendship?.let { friendshipRepository.saveAndFlush(friendship) }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // Needs separate transaction to update user
    fun deleteFriendship(friendship: Friendship?) {
        friendship?.let {
            friendshipRepository.delete(friendship)
            friendshipRepository.flush()
        }
    }

    @Throws(UserNotFoundException::class, FriendshipNotFoundException::class)
    private fun findFriendship(userId: Long, friendId: Long): Friendship {
        return friendshipRepository.findFriendshipBetweenUsers(userId, friendId)
                ?: throw FriendshipNotFoundException(userEntity(userId).username, userEntity(friendId).username)
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

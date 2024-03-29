package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.group.LocationDto
import me.cooper.rick.crowdcontrollerapi.dto.user.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.user.PasswordResetDto
import me.cooper.rick.crowdcontrollerapi.dto.user.RegistrationDto
import me.cooper.rick.crowdcontrollerapi.dto.user.UserDto
import me.cooper.rick.crowdcontrollerserver.controller.WebSocketController
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.*
import me.cooper.rick.crowdcontrollerserver.persistence.model.Friendship
import me.cooper.rick.crowdcontrollerserver.persistence.model.Role
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import me.cooper.rick.crowdcontrollerserver.persistence.repository.FriendshipRepository
import me.cooper.rick.crowdcontrollerserver.persistence.repository.RoleRepository
import me.cooper.rick.crowdcontrollerserver.persistence.repository.UserRepository
import me.cooper.rick.crowdcontrollerserver.util.RandomPasswordGenerator.generatePassword
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
internal class UserServiceImpl(private val userRepository: UserRepository,
                               private val roleRepository: RoleRepository,
                               private val friendshipRepository: FriendshipRepository,
                               private val bCryptPasswordEncoder: PasswordEncoder,
                               private val mailingService: MailingService,
                               private val webSocketController: WebSocketController) : UserService {

    override fun create(dto: RegistrationDto): UserDto = userRepository.saveAndFlush(newUser(dto)).toDto()

    override fun delete(userId: Long) {
        val user = userEntity(userId)
        userRepository.delete(user)
        userRepository.flush()
    }

    @Throws(InvalidBodyException::class, UserNotFoundException::class)
    override fun updateLocation(userId: Long, dto: LocationDto): UserDto {
        if (userId != dto.id) throw InvalidBodyException(userId, dto.id)
        val user = userEntity(userId)
        userRepository.saveAndFlush(
                user.copy(
                        latitude = dto.latitude,
                        longitude = dto.longitude,
                        lastLocationUpdate = Timestamp.valueOf(LocalDateTime.now())
                )
        )

        return user.toDto()
    }

    override fun allUsers(): List<UserDto> = userRepository.findAll().map(User::toDto)

    @Throws(UserNotFoundException::class)
    override fun user(username: String): UserDto {
        return userRepository.findByUsername(username)?.toDto()
                ?: throw UserNotFoundException(username)
    }

    @Throws(UserNotFoundException::class)
    override fun user(id: Long): UserDto = userEntity(id).toDto()

    @Throws(UserNotFoundException::class)
    override fun friends(id: Long): List<FriendDto> = user(id).friends

    @Throws(UserNotFoundException::class, FriendshipExistsException::class)
    override fun addFriend(userId: Long, friendDto: FriendDto): List<FriendDto> {
        val user = userEntity(userId)

        val friend = userRepository.findByEmailOrUsernameOrMobileNumber(friendDto.username)
                ?: throw UserNotFoundException(friendDto.username)

        if (friendshipExists(userId, friend.id)) throw FriendshipExistsException(friend.username)

        saveFriendship(Friendship(user, friend, false))

        return friends(userId)
    }

    @Throws(InvalidBodyException::class, UserNotFoundException::class, FriendshipNotFoundException::class)
    override fun updateFriendship(userId: Long, friendId: Long, friendDto: FriendDto): List<FriendDto> {
        if (friendId != friendDto.id) throw InvalidBodyException(friendId, friendDto.id)

        val friendship = findFriendship(userId, friendId)
        saveFriendship(friendship.copy(activated = friendDto.status == FriendDto.Status.CONFIRMED))

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

    override fun clearLocationOfUngroupedUsers() {
        val unGroupedUsersWithLocation = userRepository.findAllUnGroupedWithLocation()
        unGroupedUsersWithLocation.forEach {
            userRepository.save(it.copy(latitude = null, longitude = null))
        }
        userRepository.flush()
    }

    override fun requestResetPassword(dto: RegistrationDto): Boolean {
        val user = userRepository.findByEmail(dto.email) ?: throw UserNotFoundException(dto.email)

        val uuid = UUID.randomUUID().toString()
        userRepository.saveAndFlush(user.copy(passwordResetToken = uuid))
        return mailingService.sendPasswordResetMail(user, uuid)
    }

    override fun resetPassword(email: String, token: String): UserDto {
        val user = userRepository.findByEmailAndPasswordResetToken(email, token) ?: throw UserNotFoundException(email)
        val newPassword = generatePassword()
        mailingService.sendNewPasswordMail(user, newPassword)
        userRepository.saveAndFlush(user.copy(
                password = bCryptPasswordEncoder.encode(newPassword),
                passwordResetToken = null
        ))
        return user.toDto()
    }

    override fun updatePassword(id: Long, dto: PasswordResetDto): UserDto {
        if (id != dto.userId) throw InvalidBodyException(id, dto.userId)
        val user = userRepository.findOne(id) ?: throw UserNotFoundException(id)

        if (!bCryptPasswordEncoder.matches(dto.oldPassword, user.password)) {
            throw InvalidBodyException("The current password you entered is not correct! Please try again.")
        }
        if (dto.newPassword.isBlank()) throw EmptyPasswordException("You cannot enter a blank password!")

        userRepository.saveAndFlush(user.copy(password = bCryptPasswordEncoder.encode(dto.newPassword)))

        return user.toDto()
    }

    override fun sendGroupInvites() {
        val users: List<UserDto> = findAllWithPendingInvites()

        webSocketController.send(*users.toTypedArray())
    }

    private fun findAllWithPendingInvites(): List<UserDto> {
        return userRepository.findAllWithPendingInvites().map(User::toDto)
    }

    @Throws(UserNotFoundException::class, FriendshipNotFoundException::class)
    private fun findFriendship(userId: Long, friendId: Long): Friendship {
        return friendshipRepository.findFriendshipBetweenUsers(userId, friendId)
                ?: throw FriendshipNotFoundException(userEntity(userId).username, userEntity(friendId).username)
    }

    private fun newUser(dto: RegistrationDto): User {
        val user = User.fromDto(dto)
        val roles = roleRepository.findAllByNameIn(user.roles.map(Role::name)).toMutableSet()

        return user.copy(password = bCryptPasswordEncoder.encode(dto.password), roles = roles)
    }

    private fun friendshipExists(userId: Long, friendId: Long): Boolean {
        return friendshipRepository.findFriendshipBetweenUsers(userId, friendId) != null
    }

    @Throws(UserNotFoundException::class)
    private fun userEntity(id: Long): User {
        return userRepository.findOne(id) ?: throw UserNotFoundException(id)
    }

}

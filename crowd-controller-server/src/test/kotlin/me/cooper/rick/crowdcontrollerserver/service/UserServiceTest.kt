package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.group.LocationDto
import me.cooper.rick.crowdcontrollerapi.dto.user.FriendDto
import me.cooper.rick.crowdcontrollerapi.dto.user.RegistrationDto
import me.cooper.rick.crowdcontrollerserver.controller.WebSocketController
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.InvalidBodyException
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.UserNotFoundException
import me.cooper.rick.crowdcontrollerserver.persistence.model.Role
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import me.cooper.rick.crowdcontrollerserver.persistence.repository.FriendshipRepository
import me.cooper.rick.crowdcontrollerserver.persistence.repository.RoleRepository
import me.cooper.rick.crowdcontrollerserver.persistence.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@RunWith(MockitoJUnitRunner::class)
class UserServiceTest {

    @Mock private lateinit var userRepository: UserRepository
    @Mock private lateinit var roleRepository: RoleRepository
    @Mock private lateinit var friendshipRepository: FriendshipRepository
    @Spy private val bCryptPasswordEncoder: PasswordEncoder = BCryptPasswordEncoder(10)
    @Mock private lateinit var mailingService: MailingService
    @Mock private lateinit var webSocketController: WebSocketController

    @InjectMocks
    private lateinit var userService: UserServiceImpl

    @Rule
    @JvmField
    val thrown = ExpectedException.none()

    @Before
    fun setup() {

        // role repository mocks
        doReturn(listOf(Role(1L, "ROLE_USER")))
                .`when`(roleRepository).findAllByNameIn(listOf("ROLE_USER"))
    }

    @Test
    fun testCreate() {
        val userArgCaptor = ArgumentCaptor.forClass(User::class.java)
        // Given a registration dto
        val dto = RegistrationDto("test", "test","test@email.com", "07123456789")
        // And the userRepository is mocked to return a user...
        doReturn(User.fromDto(dto)).`when`(userRepository).saveAndFlush(any())

        // When creating a new user
        userService.create(dto)

        // A user is saved
        verify(userRepository, times(1)).saveAndFlush(userArgCaptor.capture())
        val savedUser = userArgCaptor.value
        assertThat(savedUser).isNotNull()
        // And has the expected details
        assertEquals(dto.username, savedUser.username)
        assertEquals(dto.email, savedUser.email)
        assertEquals(dto.mobileNumber, savedUser.mobileNumber)
        assertTrue(bCryptPasswordEncoder.matches(dto.password, savedUser.password))
        assertThat(savedUser.roles).hasSize(1)
        assertEquals(savedUser.roles.first().name, "ROLE_USER")
    }

    @Test(expected = UserNotFoundException::class)
    fun testDeleteNonExistentUser() {
        // Given the repository will return nothing when queried
        doReturn(null).`when`(userRepository).findOne(any())

        // When deleting a user that doesnt exist
        userService.delete(-1)

        // Expect a user not found exception
    }

    @Test
    fun testDelete() {
        // Given the repository will return a user given its id
        val user = User(id = 2L)
        doReturn(user).`when`(userRepository).findOne(user.id)

        // When deleting a user providing its id
        userService.delete(user.id)

        // Then the user is deleted
        verify(userRepository, times(1)).delete(user)
    }

    @Test
    fun testGetUserByUsername() {
        // Given the repository will return a user given its username
        val user = User(username = "test")
        doReturn(user).`when`(userRepository).findByUsername(user.username)

        // When retrieving the user
        val userDto = userService.user(user.username)

        // Then the user is retrieved as expected
        assertThat(userDto.username).isEqualTo(user.username)
    }

    @Test
    fun testAddFriend() {
        // Given a user and a potential friend
        val user = User(id = 2L, username = "test")
        val friend = User(id = 3L, username = "friend")
        doReturn(user).`when`(userRepository).findOne(user.id)
        doReturn(friend).`when`(userRepository).findByEmailOrUsernameOrMobileNumber(friend.username)

        userService.addFriend(user.id, FriendDto(username = friend.username))

        // Then the friendship is saved
        verify(friendshipRepository, times(1)).saveAndFlush(any())
    }

    @Test(expected = InvalidBodyException::class)
    fun testUpdateLocationThrowsInvalidBodyException() {
        // Given a user
        val user = User(id = 2L, username = "test")
        // And a location dto with a non-matching id
        val locationDto = LocationDto(1L, 1.001, 2.123)
        // And the repository returns the user
        doReturn(user).`when`(userRepository).findOne(user.id)


        // When attempting to update the user's location
        userService.updateLocation(user.id, locationDto)
        // Then An invalid body exception is thrown
    }

    @Test
    fun testUpdateLocation() {
        // Given a user and a matching locatino dto
        val user = User(id = 2L, username = "test")
        val locationDto = LocationDto(user.id, 1.001, 2.123)
        doReturn(user).`when`(userRepository).findOne(user.id)
        // And we'll capture the user
        val userArgCaptor = ArgumentCaptor.forClass(User::class.java)


        // When updating the location
        userService.updateLocation(user.id, locationDto)
        // A user is saved
        verify(userRepository, times(1)).saveAndFlush(userArgCaptor.capture())
        val savedUser = userArgCaptor.value
        assertThat(savedUser).isNotNull()
        // And its location is as expected
        assertThat(savedUser.latitude).isEqualTo(locationDto.latitude)
        assertThat(savedUser.longitude).isEqualTo(locationDto.longitude)
    }

}
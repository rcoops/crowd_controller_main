package me.cooper.rick.crowdcontrollerserver.persistence.repository

import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.Role
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import me.cooper.rick.crowdcontrollerserver.testutil.buildTestGroup
import me.cooper.rick.crowdcontrollerserver.testutil.buildTestUser
import me.cooper.rick.crowdcontrollerserver.testutil.buildTestUserList
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    private lateinit var userRole: Role

    @Before
    fun setup() {
        userRole = roleRepository.saveAndFlush(Role())
    }

    @Test
    fun testFindAllByIdIn() {
        // Given some saved users
        val users = buildTestUserList(5, 0, userRole)
                .map { userRepository.save(it) }
        userRepository.flush()

        // When retrieving those with specific ids
        val retrievedUsers = userRepository.findAllByIdIn(setOf(2,3))

        // Then they are both retrieved (and no more)
        assertThat(retrievedUsers).containsExactlyElementsOf(listOf(users[1], users[2]))
    }

    @Test
    fun testFindByEmailOrUsernameOrMobileNumber() {
        //Given a saved user
        val user = buildTestUserList(1, 0, userRole)
                .map { userRepository.save(it) }
                .first()
        userRepository.flush()

        // When finding the user by email
        val retrievedByEmail = userRepository.findByEmailOrUsernameOrMobileNumber(user.email)

        // Then the user is retrieved
        assertThat(retrievedByEmail).isEqualTo(user)

        // When finding the user by mobile
        val retrievedByMobile = userRepository.findByEmailOrUsernameOrMobileNumber(user.mobileNumber)

        // Then the user is retrieved
        assertThat(retrievedByMobile).isEqualTo(user)

        // When finding the user by username
        val retrievedByUsername = userRepository.findByEmailOrUsernameOrMobileNumber(user.username)

        // Then the user is retrieved
        assertThat(retrievedByUsername).isEqualTo(user)


        // When finding the user by username
        val nothing = userRepository.findByEmailOrUsernameOrMobileNumber("notAStoredDetail")

        // Then nothing is retrieved
        assertThat(nothing).isNull()
    }

    @Test
    fun testFindAllWithPendingInvites() {
        // Given a number of saved users, a subset of which are grouped, and a subset of those are confirmed
        val usersWithPendingInvites = buildTestUserList(2, 0, userRole)
                .map { userRepository.save(it) }
        val groupedUsers = buildTestUserList(2, 2, userRole)
                .map { userRepository.save(it.copy(groupAccepted = true)) }
        val ungroupedUsers = buildTestUserList(1, 4, userRole)
                .map { userRepository.save(it) }
        val group = groupRepository.save(Group(admin = groupedUsers[0], members = setOf(*(groupedUsers + usersWithPendingInvites).toTypedArray())))
        (usersWithPendingInvites + groupedUsers).forEach { userRepository.save(it.copy(group = group)) }
        groupRepository.flush()
        userRepository.flush()

        // When attempting to return all users that are grouped but have not accepted
        val retrievedPendingUsers = userRepository.findAllWithPendingInvites()

        // Then the retrieved users are those expected
        assertThat(retrievedPendingUsers).containsExactlyElementsOf(usersWithPendingInvites)
    }

    @Test
    fun testFindAllUnGroupedWithLocation() {
        // Given users with :
        // a group and a location
        val groupedUsersWithLocation = buildTestUserList(1, 0, userRole)
                .map { userRepository.save(it.copy(latitude = 1.01, longitude = 2.1234)) }
        // a group but no location
        val groupedUsersWithoutLocation = buildTestUserList(1, 1, userRole)
                .map { userRepository.save(it) }
        // no group group and a location (EXPECTED)
        val ungroupedUsersWithLocation = buildTestUserList(2, 2, userRole)
                .map { userRepository.save(it.copy(latitude = 1.01, longitude = 2.1234)) }
        // no group and no location
        val ungroupedUsersWithoutLocation = buildTestUserList(1, 4, userRole)
                .map { userRepository.save(it) }

        // And a group that the grouped members belong to
        val group = groupRepository.save(Group(
                admin = groupedUsersWithLocation[0],
                members = setOf(*(groupedUsersWithLocation + groupedUsersWithoutLocation).toTypedArray()))
        )
        (groupedUsersWithLocation + groupedUsersWithoutLocation).forEach { userRepository.save(it.copy(group = group)) }
        groupRepository.flush()
        userRepository.flush()

        // When finding all ungrouped users with stored location
        val retrievedUsersWithGroupButNoLocation = userRepository.findAllUnGroupedWithLocation()

        // Then the retrieved users are the expected subset of users
        assertThat(retrievedUsersWithGroupButNoLocation).containsExactlyElementsOf(ungroupedUsersWithLocation)
    }

}

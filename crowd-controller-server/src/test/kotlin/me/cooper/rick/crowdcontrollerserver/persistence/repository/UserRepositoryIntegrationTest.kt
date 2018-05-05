package me.cooper.rick.crowdcontrollerserver.persistence.repository

import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.Role
import me.cooper.rick.crowdcontrollerserver.testutil.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        userRepository.deleteAll()
        groupRepository.deleteAll()
    }

    @Test
    fun testFindAllByIdIn() {
        // Given some saved users
        val users = buildIntegrationTestUsers(*mapToName(1, 5), role = userRole)
                .map { userRepository.save(it) }
        userRepository.flush()

        // When retrieving those with specific ids
        val retrievedUsers = userRepository.findAllByIdIn(setOf(users[1].id, users[2].id))

        // Then they are both retrieved (and no more)
        assertThat(retrievedUsers).containsExactlyElementsOf(listOf(users[1], users[2]))
    }

    @Test
    fun testFindByEmailOrUsernameOrMobileNumber() {
        //Given a saved user
        val user = userRepository.saveAndFlush(buildIntegrationTestUser("1", role = userRole))

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
        val usersWithPendingInvites = buildIntegrationTestUsers("1", "2", role = userRole)
                .map { userRepository.save(it) }
        val groupedUsers = buildIntegrationTestUsers("3", "4", role = userRole)
                .map { userRepository.save(it.copy(groupAccepted = true)) }
        val ungroupedUsers = userRepository.save(buildIntegrationTestUser("5", userRole))
        val group = groupRepository.save(Group(admin = groupedUsers[0], members = mutableSetOf(*(groupedUsers + usersWithPendingInvites).toTypedArray())))
//        groupRepository.flush()
        (usersWithPendingInvites + groupedUsers).forEach { userRepository.save(it.copy(group = group)) }
//        userRepository.flush()

        // When attempting to return all users that are grouped but have not accepted
        val retrievedPendingUsers = userRepository.findAllWithPendingInvites()

        // Then the retrieved users are those expected
        assertThat(retrievedPendingUsers).containsExactlyElementsOf(usersWithPendingInvites)
    }

    @Test
    fun testFindAllUnGroupedWithLocation() {
        // Given users with :
        // a group and a location
        val groupedUsersWithLocation = buildIntegrationTestUsers("1", "2")
                .map { userRepository.save(it.copy(latitude = 1.01, longitude = 2.1234)) }
        // a group but no location
        val groupedUsersWithoutLocation = buildIntegrationTestUsers("3", "4")
                .map { userRepository.save(it) }
        // no group group and a location (EXPECTED)
        val unGroupedUsersWithLocation = buildIntegrationTestUsers("5", "6", role = userRole)
                .map { userRepository.save(it.copy(latitude = 1.01, longitude = 2.1234)) }
        // no group and no location
        val unGroupedUsersWithoutLocation = buildIntegrationTestUsers("7", "8", role = userRole)
                .map { userRepository.save(it) }

        // And a group that the grouped members belong to
        val group = groupRepository.save(Group(
                admin = groupedUsersWithLocation[0],
                members = mutableSetOf(*(groupedUsersWithLocation + groupedUsersWithoutLocation).toTypedArray()))
        )
//        groupRepository.flush()
        (groupedUsersWithLocation + groupedUsersWithoutLocation).forEach {
            userRepository.save(it.copy(group = group, roles = mutableSetOf(userRole)))
        }
//        userRepository.flush()

        // When finding all ungrouped users with stored location
        val retrievedUsersWithGroupButNoLocation = userRepository.findAllUnGroupedWithLocation()

        // Then the retrieved users are the expected subset of users
        assertThat(retrievedUsersWithGroupButNoLocation).containsExactlyElementsOf(unGroupedUsersWithLocation)
    }

}

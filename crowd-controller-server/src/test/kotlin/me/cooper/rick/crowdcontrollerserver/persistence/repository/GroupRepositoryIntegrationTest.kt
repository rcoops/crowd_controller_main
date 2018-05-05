package me.cooper.rick.crowdcontrollerserver.persistence.repository

import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.Role
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import me.cooper.rick.crowdcontrollerserver.testutil.buildTestUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@RunWith(SpringRunner::class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
internal class GroupRepositoryIntegrationTest {

    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    private var userIdCounter = 0

    private val userRole = Role()

    @Before
    fun setup() {
        userRepository.deleteAll()
        roleRepository.saveAndFlush(userRole)
    }

    @Test
    fun findByExpiryBeforeTest() {
        // Given some expired groups and live groups
        val expiredGroups = buildGroups(0, -1, -10, -60, -180, -720)
                .map { groupRepository.save(it) }
        val validGroups = buildGroups(1, 5, 60, 180, 720)
                .map { groupRepository.save(it) }
        groupRepository.flush()
        // And a time to check for (now)
        val dateToCheckExpiryBefore = buildDateTime(0)

        // When the repository is asked to retrieve expired groups
        val retrievedExpiredGroups = groupRepository.findByExpiryBefore(
                Date.from(dateToCheckExpiryBefore.atZone(ZoneId.systemDefault()).toInstant())
        )

        // Then only the expired groups are retrieved
        assertThat(retrievedExpiredGroups).containsExactlyElementsOf(expiredGroups)
    }

    private fun buildDateTime(numberOfMinutesDifference: Long): LocalDateTime {
        return LocalDateTime.now().plusMinutes(numberOfMinutesDifference)
    }

    private fun buildGroups(vararg expiryNumberOfMinutesDifference: Long): List<Group> {
        return expiryNumberOfMinutesDifference.map { buildGroup(buildDateTime(it)) }
    }

    private fun buildGroup(localDateTime: LocalDateTime): Group {
        val admin = buildTestUser("${userIdCounter++}", userRole)
        userRepository.save(admin)
        return Group(
                admin = admin,
                expiry = Timestamp.valueOf(localDateTime),
                created = Timestamp.valueOf(localDateTime.minusHours(12))
        )
    }

    private fun buildTestUser(username: String, role: Role): User {
        return User(username = username, email = "$username@email.com", mobileNumber = "0123456789$username", roles = mutableSetOf(role))
    }

}

package me.cooper.rick.crowdcontrollerserver.persistence.repository

import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@RunWith(SpringRunner::class)
@DataJpaTest
internal class GroupRepositoryIntegrationTest {

    @Autowired
    private lateinit var groupRepository: GroupRepository

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

        // Then the same number of groups are retrieved as expected
        assertThat(retrievedExpiredGroups.size).isEqualTo(expiredGroups.size)
        // And the retrieved list includes all expected expired groups
        assertThat(retrievedExpiredGroups).containsAll(expiredGroups)
        // And none of the unexpired
        assertThat(retrievedExpiredGroups).doesNotContainAnyElementsOf(validGroups)
    }

    private fun buildDateTime(numberOfMinutesDifference: Long): LocalDateTime {
        return LocalDateTime.now().plusMinutes(numberOfMinutesDifference)
    }

    private fun buildGroups(vararg expiryNumberOfMinutesDifference: Long): List<Group> {
        return expiryNumberOfMinutesDifference.map { buildGroup(buildDateTime(it)) }
    }

    private fun buildGroup(localDateTime: LocalDateTime): Group {
        return Group(expiry = Timestamp.valueOf(localDateTime), created = Timestamp.valueOf(localDateTime.minusHours(12)))
    }

}

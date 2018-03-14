package me.cooper.rick.crowdcontrollerserver.persistence.location

import com.apporiented.algorithm.clustering.Cluster
import com.apporiented.algorithm.clustering.CompleteLinkageStrategy
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm
import me.cooper.rick.crowdcontrollerserver.persistence.location.util.Point
import me.cooper.rick.crowdcontrollerserver.persistence.location.util.filter
import me.cooper.rick.crowdcontrollerserver.persistence.location.util.leaves
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.Assert.*
import kotlin.math.ceil

internal object MultiLocationResolverSpec : Spek({

    given("a cluster of clusters") {
        //                 1234567
        //                /       \
        //               /         \
        //           1234           567
        //          /    \         /   \
        //        12      34     56     7
        //       /  \    /  \   /  \
        //      1    2  3    4 5    6
        val allLeaves: List<Cluster> = (0..6).map { Cluster(it.toString()) }
        val cluster01 = Cluster("01").apply { addChildren(allLeaves[0], allLeaves[1]) }
        val cluster23 = Cluster("23").apply { addChildren(allLeaves[2], allLeaves[3]) }
        val cluster45 = Cluster("45").apply { addChildren(allLeaves[4], allLeaves[5]) }
        val cluster456 = Cluster("456").apply { addChildren(cluster45, allLeaves[6]) }
        val cluster0123 = Cluster("0123").apply { addChildren(cluster01, cluster23) }
        val cluster0123456 = Cluster("0123456").apply { addChildren(cluster0123, cluster456) }

        val expectedClustersToLeaves = allLeaves.map { it to listOf(it) }.toMap() + mapOf(
                cluster0123456 to allLeaves,
                cluster0123 to allLeaves.slice(0..3),
                cluster456 to allLeaves.slice(4..6),
                cluster01 to allLeaves.slice(0..1),
                cluster45 to allLeaves.slice(4..5)
        )
        expectedClustersToLeaves.forEach { cluster, expectedLeaves ->
            on("finding the leaves of ${cluster.name}") {
                val leaves = cluster.leaves()
                it("should contain clusters $expectedLeaves") {
                    assertEqualsIgnoringOrder(expectedLeaves, leaves)
                }
            }
        }
    }
    given("a resolver and a set of users and a set of expected results") {
        val multiLocationResolver = MultiLocationResolver(MIN_USER_PERCENTAGE, MAX_DISTANCE)
        val users = mapToUser(mapOf(
                "1" to Point(0.0, 0.0),
                "2" to Point(0.0, 1.0),
                "3" to Point(0.0, 2.0),
                "4" to Point(0.0, 3.0),
                "5" to Point(0.0, 4.0),
                "6" to Point(0.0, 5.0)
        ))
        val minUsersInCluster = ceil(users.size * MIN_USER_PERCENTAGE).toInt()
        val expectedResults = mapOf(
                "1" to doubleArrayOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0),
                "2" to doubleArrayOf(1.0, 0.0, 1.0, 2.0, 3.0, 4.0),
                "3" to doubleArrayOf(2.0, 1.0, 0.0, 1.0, 2.0, 3.0),
                "4" to doubleArrayOf(3.0, 2.0, 1.0, 0.0, 1.0, 2.0),
                "5" to doubleArrayOf(4.0, 3.0, 2.0, 1.0, 0.0, 1.0),
                "6" to doubleArrayOf(5.0, 4.0, 3.0, 2.0, 1.0, 0.0)
        )
        on("calculating a distance matrix map") {
            val distanceMatrixMap = multiLocationResolver.toDistanceMatrixMap(users)
            users.forEach {
                val username = it.username
                val expectedResult = expectedResults[username]
                it("should have an entry for $username") {
                    assertNotNull(distanceMatrixMap[username])
                }
                val expectedResultStr = expectedResult?.joinToString(", ") { it.toString() }
                it("should have $expectedResultStr for $username's distance map") {
                    assertArrayEquals(expectedResult, distanceMatrixMap[username], 0.0)
                }
            }
        }
        on("calculating most suitable cluster for group location") {
            val distanceMatrixMap = multiLocationResolver.toDistanceMatrixMap(users)
            val cluster: Cluster = DefaultClusteringAlgorithm()
                    .performClustering(distanceMatrixMap.values.toTypedArray(),
                            users.map { it.username }.toTypedArray(),
                            CompleteLinkageStrategy())
            val bestCluster = cluster.filter(minUsersInCluster, MAX_DISTANCE)
            it("should not be null") {
                assertTrue("No cluster found when expected!!", bestCluster != null)
            }
            it("should have distance less than $MAX_DISTANCE") {
                assertTrue(bestCluster!!.distance.distance < MAX_DISTANCE)
            }
            it("should have more users than $minUsersInCluster") {
                assertTrue(bestCluster!!.leaves().size > minUsersInCluster)
            }
        }
    }
    given("a group with users in and a list expected averages") {
        val multiLocationResolver = MultiLocationResolver()
        val users = mapToUser(mapOf(
                "1" to Point(8678.123123,   -14232.151254),
                "2" to Point(-1245.13123,   124315.05683452),
                "3" to Point(-21.0,         -3636.2323),
                "4" to Point(4.2315,        125.0),
                "5" to Point(2352356.2151,  -56734.2351),
                "6" to Point(2352.13525,    546238.235235)
        ))
        val group = Group(null, users.first(), users)
        val usersToExpectedAverageLocation = mapOf(
                listOf("1", "2", "3", "4", "5", "6") to Point(393687.4289572,	99345.9455693),
                listOf("1", "2", "3", "4") to           Point(1854.0558483,     26642.9183201),
                listOf("2", "3") to                     Point(-633.0656150,     60339.4122673),
                listOf("3", "4", "5", "6") to           Point(588672.8954625,	121498.1919588),
                listOf("4", "5") to                     Point(1176180.2233000,  -28304.6175500),
                listOf("1", "3", "5", "6") to           Point(590841.3683683,	117908.9041453),
                listOf("2", "4", "6") to                Point(370.4118400,	    223559.4306898),
                listOf("1", "3", "6") to                Point(3669.7527910,	    176123.2838937),
                listOf("3", "6") to                     Point(1165.5676250,	    271301.0014675)
        )
        usersToExpectedAverageLocation.forEach { userNames, expectedAverage ->
            on("building an average location dto for $userNames") {
                val dto = multiLocationResolver.buildAverageLocationDto(userNames, group)
                val lat = dto.latitude
                val long = dto.longitude
                it("should have the group's id") {
                    assertEquals(dto.id, group.id)
                }
                it("should have populated latitude and longitude") {
                    assertTrue("Invalid location - lat: $lat, long: $long", dto.hasLocation())
                }
                it("'s latitude: $lat should be approximately equal to ${expectedAverage.first}") {
                    assertEquals(expectedAverage.first, lat!!, ACCURACY_REQUIREMENT)
                }
                it("'s latitude: $long should be approximately equal to ${expectedAverage.second}") {
                    assertEquals(expectedAverage.second, long!!, ACCURACY_REQUIREMENT)
                }
            }
        }
    }
})

const val MIN_USER_PERCENTAGE = 0.5
const val MAX_DISTANCE = 4.0
const val ACCURACY_REQUIREMENT = 0.0000001 // 10 metres in worst case scenario for degrees

private fun mapToUser(userNameToLocationMap: Map<String, Point>): MutableSet<User> {
    return userNameToLocationMap
            .map { User(username = it.key, latitude = it.value.first, longitude = it.value.second) }
            .toMutableSet()
}

private fun <T> assertEqualsIgnoringOrder(expected: Collection<T>, actual: Collection<T>) {
    assertEquals("Expected size: ${expected.size}, Actual Size: ${actual.size}",
            expected.size, actual.size)

    assertTrue("Expected missing clusters: ${(actual - expected)}",
            expected.containsAll(actual))
}

private fun Cluster.addChildren(vararg clusters: Cluster) {
    this.children = clusters.toList()
}

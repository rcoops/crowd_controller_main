package me.cooper.rick.crowdcontrollerserver.persistence.location

import com.apporiented.algorithm.clustering.Cluster
import com.apporiented.algorithm.clustering.CompleteLinkageStrategy
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm
import com.google.maps.model.LatLng
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import me.cooper.rick.crowdcontrollerserver.persistence.location.util.filter
import me.cooper.rick.crowdcontrollerserver.persistence.location.util.leaves
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import kotlin.math.ceil

internal class MultiLocationResolverTest() {

    companion object {
        private const val MIN_USER_PERCENTAGE = 0.5
        private const val MAX_DISTANCE_METRES = 110.0
        private const val LAT_LONG_ERROR_MARGIN = 0.0000001 // approx 10 metres in worst case scenario
        private const val DISTANCE_ERROR_MARGIN = 0.01
    }

    @Test
    fun testCorrectLeavesAreStripped() {
        // Given a cluster of clusters with the following structure
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

            // When finding the cluster's leaves
            val leaves = cluster.leaves()

            // Then it should contain the expected clusters
            assertThat(leaves).containsExactlyInAnyOrder(*expectedLeaves.toTypedArray())
        }

    }

    @Test
    fun testMatrixMapIsCreatedAsExpected() {
        // Given a resolver, set of users and a set of expected results
        val multiLocationResolver = MultiLocationResolver(
                MIN_USER_PERCENTAGE,
                MAX_DISTANCE_METRES
        )
        val users = mapToUser(mapOf(
                "1" to LatLng(53.485804, -2.273955),
                "2" to LatLng(53.485363, -2.274443),
                "3" to LatLng(53.485823, -2.274722),
                "4" to LatLng(53.486206, -2.273687),
                "5" to LatLng(53.502470, -2.305452), // outside
                "6" to LatLng(53.481891, -2.316782)  // outside
        ))
        val minUsersInCluster = ceil(users.size * MIN_USER_PERCENTAGE).toInt()

        // https://www.movable-type.co.uk/scripts/latlong-vincenty.html
        // http://www.contextures.com/excellatitudelongitude.html

        // @formatter:off
        val expectedDistanceMatrixMap = mapOf(
                "1" to doubleArrayOf(0.0, 58.808, 50.958, 48.148, 2794.681, 2876.201),
                "2" to doubleArrayOf(58.808, 0.0, 54.443, 106.4, 2803.643, 2837.097),
                "3" to doubleArrayOf(50.958, 54.443, 0.0, 80.853, 2755.39, 2826.208),
                "4" to doubleArrayOf(48.148, 106.4, 80.853, 0.0, 2778.652, 2900.845),
                "5" to doubleArrayOf(2794.681, 2803.643, 2755.39, 2778.652, 0.0, 2410.641),
                "6" to doubleArrayOf(2876.201, 2837.097, 2826.208, 2900.845, 2410.641, 0.0)
        )
        // @formatter:on

        // When calculating a distance matrix map
        users.map(User::username).forEach { username ->
            val expectedResult = expectedDistanceMatrixMap[username]

            // When calculating most suitable cluster for group location
            val distanceMatrixMap = multiLocationResolver.toDistanceMatrixMap(users)

            // Then it should have a username
            assertNotNull(distanceMatrixMap[username])

            // And it should have the expected result within a margin
            assertArrayEquals(expectedResult, distanceMatrixMap[username], DISTANCE_ERROR_MARGIN) // 0.01 metres

            // Given a parent cluster built using the standard technique
            val cluster: Cluster = DefaultClusteringAlgorithm()
                    .performClustering(distanceMatrixMap.values.toTypedArray(),
                            users.map { it.username }.toTypedArray(),
                            CompleteLinkageStrategy())
            // When finding the most suitable cluster
            val bestCluster = cluster.filter(minUsersInCluster, MAX_DISTANCE_METRES)

            // Then it should not be null
            assertTrue("No cluster found when expected!!", bestCluster != null)

            // And it should have distance less than the max allowed
            assertTrue(
                    "${bestCluster!!.name} has distance is more than $MAX_DISTANCE_METRES metres",
                    bestCluster.distance.distance < MAX_DISTANCE_METRES
            )
            // And it should have more than minimum allowed users
            assertTrue(
                    "${bestCluster.name} has less than than $minUsersInCluster users",
                    bestCluster.leaves().size > minUsersInCluster
            )
        }
    }

    @Test
    fun testGetAverageLatLngRetrievesExpectedAverage() {
        // Given a group with users in and a list expected averages
        val multiLocationResolver = MultiLocationResolver()

        // @formatter:off
        val users = mapToUser(mapOf(
                "1" to LatLng(8678.123123,   -14232.151254),
                "2" to LatLng(-1245.13123,   124315.05683452),
                "3" to LatLng(-21.0,         -3636.2323),
                "4" to LatLng(4.2315,        125.0),
                "5" to LatLng(2352356.2151,  -56734.2351),
                "6" to LatLng(2352.13525,    546238.235235)
        ))
        val usersToExpectedAverageLocation = mapOf(
                listOf("1", "2", "3", "4", "5", "6") to LatLng(393687.4289572,	99345.9455693),
                listOf("1", "2", "3", "4") to           LatLng(1854.0558483,     26642.9183201),
                listOf("2", "3") to                     LatLng(-633.0656150,     60339.4122673),
                listOf("3", "4", "5", "6") to           LatLng(588672.8954625,	121498.1919588),
                listOf("4", "5") to                     LatLng(1176180.2233000,  -28304.6175500),
                listOf("1", "3", "5", "6") to           LatLng(590841.3683683,	117908.9041453),
                listOf("2", "4", "6") to                LatLng(370.4118400,	    223559.4306898),
                listOf("1", "3", "6") to                LatLng(3669.7527910,     176123.2838937),
                listOf("3", "6") to                     LatLng(1165.5676250,     271301.0014675)
        )
        // @formatter:on
        val group = Group(null, users.first(), users)

        usersToExpectedAverageLocation.forEach { userNames, expectedAverage ->
            // When  building an average location dto for the users
            val latLng = multiLocationResolver.getAverateLatLng(userNames, group)

            // Then its latitude should be approximately equal to the expected average
            Assert.assertEquals(expectedAverage.lat, latLng.lat, LAT_LONG_ERROR_MARGIN)

            // And its longitude should be approximately equal to the expected average
            Assert.assertEquals(expectedAverage.lng, latLng.lng, LAT_LONG_ERROR_MARGIN)
        }
    }

    private fun mapToUser(userNameToLocationMap: Map<String, LatLng>): MutableSet<User> {
        return userNameToLocationMap
                .map { User(username = it.key, latitude = it.value.lat, longitude = it.value.lng) }
                .toMutableSet()
    }

    private fun Cluster.addChildren(vararg clusters: Cluster) {
        children.addAll(clusters.toList())
    }

}

package me.cooper.rick.crowdcontrollerserver.persistence.location

import com.apporiented.algorithm.clustering.Cluster
import com.apporiented.algorithm.clustering.CompleteLinkageStrategy
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm
import me.cooper.rick.crowdcontrollerapi.dto.group.LocationDto
import me.cooper.rick.crowdcontrollerserver.persistence.location.util.DistanceUtil.average
import me.cooper.rick.crowdcontrollerserver.persistence.location.util.leaves
import me.cooper.rick.crowdcontrollerserver.persistence.location.util.filter
import me.cooper.rick.crowdcontrollerserver.persistence.location.util.DistanceUtil.distance
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import kotlin.math.ceil

typealias DistanceMatrixMap = Map<String, DoubleArray>
typealias DistanceMatrix = DoubleArray
typealias Distance = Double

internal class MultiLocationResolver(private val minUsersPercentage: Double = 0.5,
                                     private val maxDistanceMetres: Double = 200.0) : LocationResolver {

    override fun location(group: Group): LocationDto {
        val distanceMatrixMap = toDistanceMatrixMap(group.members)
        val names = distanceMatrixMap.keys.toTypedArray()
        val minNodes = ceil(names.size * minUsersPercentage).toInt()
        val cluster: Cluster = DefaultClusteringAlgorithm()
                .performClustering(distanceMatrixMap.values.toTypedArray(), names, CompleteLinkageStrategy())

        val bestClusterMatch = cluster.filter(minNodes, maxDistanceMetres)

        return when (bestClusterMatch) {
            null -> LocationDto(group.id, group.admin!!.latitude, group.admin!!.longitude) // default to admin if not within limits
            else -> buildAverageLocationDto(bestClusterMatch.leaves().map { it.name }, group)
        }
    }

    internal fun toDistanceMatrixMap(users: Set<User>): DistanceMatrixMap {
        fun User.getDistanceMatrix(users: Set<User>): DistanceMatrix {
            fun User.findDistance(other: User): Distance {
                fun isMissingLocation(user: User, other: User) = !user.hasLocation() || !other.hasLocation()
                fun User.distance(other: User): Distance {
                    return distance(latitude!!, longitude!!, other.latitude!!, other.longitude!!)
                }

                return if (isMissingLocation(this, other)) Distance.MAX_VALUE else distance(other)
            }
            return users.map { findDistance(it) }.toDoubleArray()
        }

        return users.map { it.username to it.getDistanceMatrix(users) }.toMap()
    }

    internal fun buildAverageLocationDto(userNames: List<String>, group: Group): LocationDto {
        val allLocations = group.members
                .filter { it.username in userNames }
                .map { Pair(it.latitude, it.longitude) }
        val averageLocation = average(*allLocations.toTypedArray())

        return LocationDto(group.id, averageLocation.first, averageLocation.second)
    }

}

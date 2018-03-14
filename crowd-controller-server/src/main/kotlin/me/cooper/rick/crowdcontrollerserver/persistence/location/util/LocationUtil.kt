package me.cooper.rick.crowdcontrollerserver.persistence.location.util

import com.apporiented.algorithm.clustering.Cluster
import me.cooper.rick.crowdcontrollerserver.persistence.location.Distance

typealias Point = Pair<Double, Double>
typealias OptionalPoint = Pair<Double?, Double?>
typealias xCoOrdinate = Double
typealias yCoOrdinate = Double

internal object DistanceUtil {

    private fun squaredDifference(first: Double, second: Double) = Math.pow(first - second, 2.0)

    internal fun distance(pointX: xCoOrdinate, pointY: yCoOrdinate,
                          otherX: xCoOrdinate, otherY: yCoOrdinate): Distance {
        return Math.sqrt(squaredDifference(pointX, otherX) + squaredDifference(pointY, otherY))
    }

    private fun sum(vararg points: OptionalPoint): Point {
        operator fun Point.plus(other: OptionalPoint): Point {
            return kotlin.Pair(first + (other.first ?: 0.0), second + (other.second ?: 0.0))
        }

        return points.fold(Point(0.0, 0.0), { current, next -> current + next })
    }

    internal fun average(vararg points: OptionalPoint): Point {
        operator fun Point.div(numberOfPairs: Int): Point {
            return kotlin.Pair(first / numberOfPairs, second / numberOfPairs)
        }

        return sum(*points) / points.size
    }

}

internal fun Cluster.leaves(): List<Cluster> {
    @Suppress("NO_TAIL_CALLS_FOUND", "NON_TAIL_RECURSIVE_CALL") // Actually are tail calls hidden in inner functions
    tailrec fun stripLeaves(cluster: Cluster?, leaves: List<Cluster>): List<Cluster> {
        fun stripFirstChild(cluster: Cluster, leaves: List<Cluster>) = stripLeaves(cluster.children[0], leaves)
        fun stripBothChildren(cluster: Cluster): List<Cluster> {
            return stripFirstChild(cluster, stripLeaves(cluster.children[1], leaves))
        }
        return when {
            cluster == null -> leaves
            cluster.isLeaf -> leaves + cluster
            cluster.children.size == 2 -> stripBothChildren(cluster)
            else -> stripFirstChild(cluster, leaves)
        }
    }
    return stripLeaves(this, emptyList())
}

internal fun Cluster.filter(minLeaves: Int, maxDistance: Double): Cluster? {
    @Suppress("NON_TAIL_RECURSIVE_CALL")
    tailrec fun filterCluster(cluster: Cluster?): Cluster? {
        return when {
            cluster == null || cluster.countLeafs() < minLeaves -> null
            cluster.distance.distance < maxDistance -> cluster
            cluster.children.size == 2 -> {
                maxOf(
                        filterCluster(cluster.children[0]),
                        filterCluster(cluster.children[1]),
                        compareBy<Cluster?> { it?.distance?.weight }
                                .thenByDescending { it?.distance?.distance }
                )
            }
            else -> filterCluster(cluster.children[0])
        }
    }
    return filterCluster(this)
}

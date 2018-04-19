package me.cooper.rick.crowdcontrollerserver.persistence.location.util

import com.apporiented.algorithm.clustering.Cluster
import com.google.maps.model.LatLng
import me.cooper.rick.crowdcontrollerapi.dto.group.LocationDto
import me.cooper.rick.crowdcontrollerserver.persistence.location.Distance
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import java.lang.Math.*

internal object DistanceUtil {
    /*
     * Copyright (C) 2007 The Android Open Source Project
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    /* Original retrieved from http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/5.1.1_r1/android/location/Location.java#Location.computeDistanceAndBearing%28double%2Cdouble%2Cdouble%2Cdouble%2Cfloat%5B%5D%29 */
    private const val WGS84_MAJOR_AXIS = 6378137.0
    private const val WGS84_SEMI_MAJOR_AXIS = 6356752.3142
    private const val MAX_ITERATIONS = 20

    private const val F = (WGS84_MAJOR_AXIS - WGS84_SEMI_MAJOR_AXIS) / WGS84_MAJOR_AXIS
    private val aSqMinusBSqOverBSq = (pow(WGS84_MAJOR_AXIS, 2.0) - pow(WGS84_SEMI_MAJOR_AXIS, 2.0)) / pow(WGS84_SEMI_MAJOR_AXIS, 2.0)

    internal fun distance(latLng: LatLng, otherLatLng: LatLng): Distance {
        // Based on http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
        // using the "Inverse Formula" (section 4)

        val lat1 = toRadians(latLng.lat)
        val lon1 = toRadians(latLng.lng)
        val lat2 = toRadians(otherLatLng.lat)
        val lon2 = toRadians(otherLatLng.lng)

        val initialGuess = lon2 - lon1
        var A = 0.0
        val U1 = atan((1.0 - F) * tan(lat1))
        val U2 = atan((1.0 - F) * tan(lat2))

        val cosU1 = cos(U1)
        val cosU2 = cos(U2)
        val sinU1 = sin(U1)
        val sinU2 = sin(U2)
        val cosU1cosU2 = cosU1 * cosU2
        val sinU1sinU2 = sinU1 * sinU2

        var sigma = 0.0
        var deltaSigma = 0.0
        var cosSqAlpha: Double
        var cos2SM: Double
        var cosSigma: Double
        var sinSigma: Double
        var cosLambda: Double
        var sinLambda: Double

        var lambda = initialGuess
        for (i in 0 until MAX_ITERATIONS) {
            val lambdaOrig = lambda
            cosLambda = cos(lambda)
            sinLambda = sin(lambda)
            val t1 = cosU2 * sinLambda
            val t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda
            val sinSqSigma = t1 * t1 + t2 * t2 // (14)
            sinSigma = sqrt(sinSqSigma)
            cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda // (15)
            sigma = atan2(sinSigma, cosSigma) // (16)
            val sinAlpha = if (sinSigma == 0.0) 0.0 else cosU1cosU2 * sinLambda / sinSigma // (17)
            cosSqAlpha = 1.0 - sinAlpha * sinAlpha
            cos2SM = if (cosSqAlpha == 0.0) 0.0 else cosSigma - 2.0 * sinU1sinU2 / cosSqAlpha // (18)

            val uSquared = cosSqAlpha * aSqMinusBSqOverBSq // defn
            A = 1 + uSquared / 16384.0 * (4096.0 + uSquared * (-768 + uSquared * (320.0 - 175.0 * uSquared))) // (3)
            val b = uSquared / 1024.0 * (256.0 + uSquared * (-128.0 + uSquared * (74.0 - 47.0 * uSquared))) // (4)
            val c = F / 16.0 * cosSqAlpha * (4.0 + F * (4.0 - 3.0 * cosSqAlpha)) // (10)
            val cos2SMSq = cos2SM * cos2SM
            deltaSigma = b * sinSigma * (
                    cos2SM + b / 4.0 * (
                            cosSigma * (-1.0 + 2.0 * cos2SMSq) - b / 6.0 * cos2SM * (-3.0 + 4.0 * sinSigma * sinSigma) *
                                    (-3.0 + 4.0 * cos2SMSq)
                            )
                    ) // (6)

            lambda = initialGuess + (1.0 - c) * F * sinAlpha * (
                    sigma + c * sinSigma * (cos2SM + c * cosSigma * (-1.0 + 2.0 * cos2SM * cos2SM))
                    ) // (11)

            val delta = (lambda - lambdaOrig) / lambda
            if (abs(delta) < 1.0e-12) break
        }

        return WGS84_SEMI_MAJOR_AXIS * A * (sigma - deltaSigma)
    }

    internal fun average(vararg points: LatLng?): LatLng {
        fun sum(vararg points: LatLng?): LatLng {
            operator fun LatLng.plus(other: LatLng?): LatLng {
                return LatLng(lat + (other?.lat ?: 0.0), lng + (other?.lng ?: 0.0))
            }

            return points.fold(LatLng(0.0, 0.0), { current, next -> current + next })
        }

        operator fun LatLng.div(numberOfPairs: Int): LatLng {
            return LatLng(lat / numberOfPairs, lng / numberOfPairs)
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


internal fun buildLocationFromAdmin(group: Group, admin: User): LocationDto {
    return LocationDto(
            id = group.id,
            latitude = group.admin!!.latitude!!,
            longitude = admin.longitude!!,
            lastUpdate = admin.lastLocationUpdate?.toLocalDateTime()
    )

}

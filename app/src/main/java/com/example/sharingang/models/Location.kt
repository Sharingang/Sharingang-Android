package com.example.sharingang.models

import com.example.sharingang.utils.degreesToRadians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * The radius of the earth, in meters
 */
private const val EARTH_RADIUS: Double = 6371e3

/**
 * Location represents a single point on the Earth.
 *
 * We use a latitude, and a longitude, both doubles. Now, doubles can be somewhat misleading,
 * because the precision of the sensor data we receive is actually less accurate.
 * On the other hand, it can't actually hurt to use more accurate numbers when doing
 * calculations. We also don't perform many expensive calculations, so the use of Double
 * isn't very onerous either.
 */
data class Location(val lat: Double, val lon: Double) {
    /**
     * This returns the distance along a great circle from this location to that location
     *
     * This is "as the crow flies", in the sense that the topography of the earth isn't
     * taken into account, and the Earth is treated as a sphere.
     *
     * Unfortunately, in practice not only is the Earth not a sphere, but an elongated
     * ellipsoid, because of its rotation, but there are also troublesome things like
     * mountains, buildings, hills, etc.
     *
     * This distance is still a reasonable approximation.
     *
     * @param that the location to calculate the distance to, compared to this location
     * @return the distance to that location, in meters
     */
    fun crowDistance(that: Location): Double {
        val phi1 = degreesToRadians(this.lat)
        val phi2 = degreesToRadians(that.lat)
        val deltaPhi = degreesToRadians(that.lat - this.lat)
        val deltaLambda = degreesToRadians(that.lon - this.lon)

        val sinDeltaPhi = sin(deltaPhi / 2.0)
        val sinDeltaLambda = sin(deltaLambda / 2.0)

        val a =
            sinDeltaPhi * sinDeltaPhi + cos(phi1) * cos(phi2) * sinDeltaLambda * sinDeltaLambda
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS * c
    }

    /**
     * This calculates the heading required to orient yourself towards another location.
     *
     * @param that the location to orient yourself to, from this location
     * @return the required heading
     */
    fun requiredHeading(that: Location): Heading {
        val phi1 = degreesToRadians(this.lat)
        val phi2 = degreesToRadians(that.lat)
        val deltaLambda = degreesToRadians(that.lon - this.lon)

        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        val theta = atan2(y, x)
        return Heading(theta)
    }
}
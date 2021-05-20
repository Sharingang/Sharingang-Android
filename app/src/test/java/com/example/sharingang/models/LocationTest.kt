package com.example.sharingang.models


import org.junit.Test
import kotlin.math.abs

class LocationTest {
    private fun approxEqual(a: Double, b: Double): Boolean {
        return abs(a - b) < 1e-5
    }

    @Test
    fun distanceBetweenSameLocationIsZero() {
        val location = Location(24.0, 30.0)
        assert(approxEqual(location.crowDistance(location), 0.0))
        val location2 = Location(100.0, 20.0)
        assert(approxEqual(location2.crowDistance(location2), 0.0))
    }

    @Test
    fun headingBetweenLatitudesIsZero() {
        val location1 = Location(20.0, 30.0)
        val location2 = Location(40.0, 30.0)
        assert(approxEqual(location1.requiredHeading(location2).azimuth, 0.0))
    }
}
package com.example.sharingang.ar

/**
 * Location represents a single point on the Earth.
 *
 * We use a latitude, and a longitude, both doubles. Now, doubles can be somewhat misleading,
 * because the precision of the sensor data we receive is actually less accurate.
 * On the other hand, it can't actually hurt to use more accurate numbers when doing
 * calculations. We also don't perform many expensive calculations, so the use of Double
 * isn't very onerous either.
 */
data class Location(val lat: Double, val lon: Double)
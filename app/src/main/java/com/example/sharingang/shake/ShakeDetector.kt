package com.example.sharingang.shake

import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * This is the force (in multiples of Earth's gravity) necessary to trigger a shake.
 */
private const val SHAKE_THRESHOLD_GRAVITY = 2.2F

/**
 * This controls how long we have to wait between shakes
 */
private const val SHAKE_WAIT = 1000

/**
 * ShakeDetector analyzes sensor events with timestamps, and detects shakes.
 *
 * It interprets raw accelerometer data, and figures out when to declare that a shake
 * happened, based on how hard the shake was, when the last detected shake happened,
 * etc.
 */
internal class ShakeDetector {
    // This holds the last time where a shake happened
    private var shakeTimestamp: Long = 0

    /**
     * Detect if a shake has happened, based on a new accelerometer reading.
     *
     * @param x acceleration on the x axis (m/s^2)
     * @param y acceleration on the y axis (m/s^2)
     * @param z acceleration on the z axis (m/s^2)
     * @param now the current time, in milliseconds
     * @return true if a shake has happened, false otherwise
     */
    fun detect(x: Float, y: Float, z: Float, now: Long): Boolean {
        val gForce = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH
        // Ignore shakes without enough force
        if (gForce < SHAKE_THRESHOLD_GRAVITY) {
            return false
        }
        // Ignore shakes that happen too close to each other
        if (shakeTimestamp + SHAKE_WAIT > now) {
            return false
        }
        shakeTimestamp = now
        return true
    }
}
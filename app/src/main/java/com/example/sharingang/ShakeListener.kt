package com.example.sharingang

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
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
 * The type of callback to react to shakes
 */
typealias OnShakeHandler = () -> Unit

/**
 * ShakeListener allows us to detect and react to the shaking of the device.
 *
 * Android doesn't natively provide us with a kind of "shake" event that we can listen
 * to. Instead, we need to listen to the accelerometer, and manually detect
 * a shake ourselves. This class encapsulates all of that logic.
 */
class ShakeListener : SensorEventListener {
    var onShakeHandler: OnShakeHandler? = null

    // This holds the last time where a shake happened
    private var shakeTimestamp: Long = 0

    override fun onSensorChanged(event: SensorEvent?) {
        if (onShakeHandler == null || event == null) {
            return
        }
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val gForce = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH
        // Ignore shakes without enough force
        if (gForce < SHAKE_THRESHOLD_GRAVITY) {
            return
        }
        // Ignore shakes that happen too close to each other
        val now = System.currentTimeMillis()
        if (shakeTimestamp + SHAKE_WAIT > now) {
            return
        }
        shakeTimestamp = now
        // We've already checked that onShakeHandler isn't null above
        onShakeHandler!!()
    }

    /**
     * This gets called when the accuracy of the sensor we're listening to changes.
     *
     * In our case, we simply ignore this event.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
package com.example.sharingang.shake

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

/**
 * The type of callback to react to shakes
 */
typealias OnShakeHandler = () -> Unit

/**
 * ShakeListener listens to sensor events from Android, triggering a callback when shakes happen.
 *
 * This is necessary, because Android doesn't natively have functionality to detect
 * shakes. Instead, we need to use the accelerometer, and manually detect them.
 */
class ShakeListener(private val onShake: OnShakeHandler) : SensorEventListener {
    // We use this to tell of a sensor event represents a shake
    private val detector = ShakeDetector()

    /**
     * This gets called when a new sensor value happens
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }
        val now = System.currentTimeMillis()
        if (detector.detect(event.values[0], event.values[1], event.values[2], now)) {
            onShake()
        }
    }

    /**
     * This gets called when the accuracy of the sensor we're listening to changes.
     *
     * In our case, we simply ignore this event.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
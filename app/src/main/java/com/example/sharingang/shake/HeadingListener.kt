package com.example.sharingang.shake

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.sharingang.models.Heading

/**
 * Represents the type of callback for when the heading changes.
 */
typealias HeadingCallback = (Heading) -> Unit

/**
 * A class used to listen for changes in heading direction.
 */
class HeadingListener(private val headingCallback: HeadingCallback) : SensorEventListener {
    fun registerWith(manager: SensorManager) {
        manager.registerListener(
            this,
            manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
        manager.registerListener(
            this,
            manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    // Our current reading of the acceloremeter
    private var gravity: FloatArray? = null

    // Our current reading of the magnetic field
    private var magnetic: FloatArray? = null

    /**
     * This gets called when a new reading happens
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values
        }
        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetic = event.values
        }
        if (gravity == null || magnetic == null) {
            return
        }
        val r = FloatArray(9)
        val i = FloatArray(9)
        if (!SensorManager.getRotationMatrix(r, i, gravity, magnetic)) {
            return
        }
        val orientation = FloatArray(3)
        SensorManager.getOrientation(r, orientation)
        val azimuth = orientation[0].toDouble()
        headingCallback(Heading(azimuth))
    }

    /**
     * This gets called when the accuracy of the sensor changes, but we don't care
     * about that event.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
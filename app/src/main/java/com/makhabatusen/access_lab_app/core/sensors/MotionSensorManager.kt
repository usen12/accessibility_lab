package com.makhabatusen.access_lab_app.core.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Detects shake gestures using the device accelerometer.
 * The triggered functionality should always have an accessible non-motion alternative.
 */
class MotionSensorManager(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val appContext = context.applicationContext
    private val sensorManager =
        appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastShakeTime: Long = 0L

    fun start() {
        accelerometer?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gForce = sqrt((x * x + y * y + z * z).toDouble()) / SensorManager.GRAVITY_EARTH

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            val now = System.currentTimeMillis()
            if (lastShakeTime + SHAKE_SLOP_TIME_MS < now) {
                lastShakeTime = now
                onShake()
            }
        }
    }

    companion object {
        private const val SHAKE_THRESHOLD_GRAVITY = 2.7f
        private const val SHAKE_SLOP_TIME_MS = 500L
    }
}
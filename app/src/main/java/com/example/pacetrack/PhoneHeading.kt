
package com.example.pacetrack

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun rememberPhoneHeading(): Float {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val heading = remember { mutableFloatStateOf(0f) }

    DisposableEffect(context, lifecycleOwner) {

        val sensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val rotationSensor =
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val listener = object : SensorEventListener {

            override fun onSensorChanged(event: SensorEvent) {

                val rotationMatrix = FloatArray(9)
                val orientation = FloatArray(3)

                SensorManager.getRotationMatrixFromVector(
                    rotationMatrix,
                    event.values
                )

                SensorManager.getOrientation(
                    rotationMatrix,
                    orientation
                )

                val degrees =
                    Math.toDegrees(orientation[0].toDouble()).toFloat()

                heading.floatValue = (degrees + 360f) % 360f
            }

            override fun onAccuracyChanged(
                sensor: Sensor?,
                accuracy: Int
            ) = Unit
        }

        var registered = false

        fun startSensor() {
            if (!registered && rotationSensor != null) {
                registered = sensorManager.registerListener(
                    listener,
                    rotationSensor,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        }

        fun stopSensor() {
            if (registered) {
                sensorManager.unregisterListener(listener)
                registered = false
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> startSensor()
                Lifecycle.Event.ON_STOP -> stopSensor()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        if (lifecycleOwner.lifecycle.currentState.isAtLeast(
                Lifecycle.State.STARTED
            )
        ) {
            startSensor()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            stopSensor()
        }
    }

    return heading.floatValue
}

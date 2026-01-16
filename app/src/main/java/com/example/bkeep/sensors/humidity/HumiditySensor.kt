package com.example.bkeep.sensors.humidity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import kotlin.random.Random

class HumiditySensor(
    context: Context,
    private val onHumidityChanged: (Float) -> Unit
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val humiditySensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)

    private val handler = Handler(Looper.getMainLooper())
    private var simulationRunning = false

    fun start() {
        if (humiditySensor != null) {
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL
            )
        } else {
            startSimulation()
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        stopSimulation()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_RELATIVE_HUMIDITY) {
            onHumidityChanged(event.values[0])
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    private fun startSimulation() {
        if (simulationRunning) return
        simulationRunning = true

        handler.post(object : Runnable {
            override fun run() {
                if (!simulationRunning) return

                val simulatedHumidity =
                    Random.nextFloat() * (75f - 40f) + 40f

                onHumidityChanged(simulatedHumidity)
                // ponovi na 10 sekund
                handler.postDelayed(this, 10_000)
            }
        })
    }

    private fun stopSimulation() {
        simulationRunning = false
        handler.removeCallbacksAndMessages(null)
    }
}

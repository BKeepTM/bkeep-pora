package com.example.bkeep.sensors.humidity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class HumiditySensor(
    context: Context,
    private val onHumidityChanged: (Float) -> Unit
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val humiditySensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)

    fun start() {
        humiditySensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_RELATIVE_HUMIDITY) {
            val humidityValue = event.values[0]
            onHumidityChanged(humidityValue)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }
}

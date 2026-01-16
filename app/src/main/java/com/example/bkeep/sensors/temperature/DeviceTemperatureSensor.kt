package com.example.bkeep.sensors.temperature

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class DeviceTemperatureSensor(
    private val context: Context,
    private val onTemperatureChanged: (Float) -> Unit
) {

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            val temp = intent?.getIntExtra(
                BatteryManager.EXTRA_TEMPERATURE,
                -1
            ) ?: return

            if (temp > 0) {
                onTemperatureChanged(temp / 10f) // deci-C → °C
            }
        }
    }

    fun start() {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(batteryReceiver, filter)
    }

    fun stop() {
        context.unregisterReceiver(batteryReceiver)
    }
}

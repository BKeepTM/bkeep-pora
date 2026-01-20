package com.example.bkeep.sensors

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bkeep.databinding.FragmentSensorInfoBinding
import com.example.bkeep.sensors.humidity.HumiditySensor
import com.example.bkeep.sensors.light.LightSensor
import com.example.bkeep.sensors.temperature.DeviceTemperatureSensor
import com.example.bkeep.worker.SensorUploadWorker
import androidx.core.content.edit

class SensorInfoFragment : Fragment() {

    private var _binding: FragmentSensorInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var brightnessFrequencyTextView: TextView
    private lateinit var humidityFrequencyTextView: TextView
    private lateinit var temperatureFrequencyTextView: TextView

    private lateinit var lightSensor: LightSensor
    private lateinit var humiditySensor: HumiditySensor
    private lateinit var deviceTemperatureSensor: DeviceTemperatureSensor
    private val PREF_NAME = "BKeepSensorPrefs"
    private val KEY_FREQ_LIGHT = "freq_light"
    private val KEY_FREQ_HUMIDITY = "freq_humidity"
    private val KEY_FREQ_TEMP = "freq_temp"

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        brightnessFrequencyTextView = binding.LightFrequencyText
        humidityFrequencyTextView = binding.humidityFrequencyText
        temperatureFrequencyTextView = binding.temperatureFrequencyText

        setupFrequencyPicker(brightnessFrequencyTextView, KEY_FREQ_LIGHT)
        setupFrequencyPicker(humidityFrequencyTextView, KEY_FREQ_HUMIDITY)
        setupFrequencyPicker(temperatureFrequencyTextView, KEY_FREQ_TEMP)


        lightSensor = LightSensor(requireContext()) { lightValue ->
            binding.lightSensorDisplay.text = "Brightness: $lightValue lx"
        }

        humiditySensor = HumiditySensor(requireContext()) { humidityValue ->
            val formatted = String.format("%.2f", humidityValue)
            binding.humiditySensorDisplay.text = "Humidity: $formatted %"
        }

        deviceTemperatureSensor = DeviceTemperatureSensor(requireContext()) { temp ->
            binding.temperatureSensorDisplay.text = "Temperature: $temp °C"
        }
    }

    private fun setupFrequencyPicker(textView: TextView, prefKey: String) {
        // 1. Load previously saved value (or default)
        val savedValue = sharedPreferences.getString(prefKey, null)
        if (savedValue != null) {
            textView.text = savedValue
        } else {
            textView.text = "Set Frequency" // Default text if nothing saved
        }
        textView.setOnClickListener {
            val timePicker = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->

                val formattedText = formatFrequencyText(hourOfDay, minute)

                textView.text = formattedText
                sharedPreferences.edit {
                    putString(prefKey, formattedText)
                }
                scheduleSensorWorker(hourOfDay, minute)

            }, 0, 10, true)

            timePicker.setTitle("Select Interval")
            timePicker.show()
        }
    }
    private fun formatFrequencyText(hour: Int, minute: Int): String {
        val sb = StringBuilder("Frequency: Every ")

        if (hour > 0) {
            sb.append("$hour hour")
            if (hour > 1) sb.append("s")
            if (minute > 0) sb.append(" and ")
        }

        if (minute > 0 || hour == 0) {
            sb.append("$minute minute")
            if (minute != 1) sb.append("s")
        }

        return sb.toString()
    }

    override fun onResume() {
        super.onResume()
        lightSensor.start()
        humiditySensor.start()
        deviceTemperatureSensor.start()
    }

    override fun onPause() {
        super.onPause()
        lightSensor.stop()
        humiditySensor.stop()
        deviceTemperatureSensor.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun scheduleSensorWorker(hours: Int, minutes: Int) {
        val totalMinutes = (hours * 60) + minutes
        val safeInterval = if (totalMinutes < 15) 15L else totalMinutes.toLong()

        val workRequest = PeriodicWorkRequestBuilder<SensorUploadWorker>(
            safeInterval, TimeUnit.MINUTES
        )
            // .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()) // v primeru da ni interneta.
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "BKeepSensorSync",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
         Toast.makeText(context, "Background sync set to $safeInterval mins", Toast.LENGTH_SHORT).show()
    }
}
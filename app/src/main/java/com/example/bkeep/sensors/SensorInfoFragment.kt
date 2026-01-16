package com.example.bkeep.sensors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bkeep.databinding.FragmentSensorInfoBinding
import com.example.bkeep.sensors.humidity.HumiditySensor
import com.example.bkeep.sensors.light.LightSensor

class SensorInfoFragment : Fragment() {

    private var _binding: FragmentSensorInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var lightSensor: LightSensor
    private lateinit var humiditySensor : HumiditySensor

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

        lightSensor = LightSensor(requireContext()) {
            lightValue -> binding.lightSensorDisplay.text = "Brightness: $lightValue lx"
        }
        humiditySensor = HumiditySensor(requireContext()){
            humidityValue -> binding.humiditySensorDisplay.text = "Humidity: $humidityValue %"
        }
    }

    override fun onResume() {
        super.onResume()
        lightSensor.start()
        humiditySensor.start()
    }

    override fun onPause() {
        super.onPause()
        lightSensor.stop()
        humiditySensor.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

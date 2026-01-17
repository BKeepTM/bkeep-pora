package com.example.bkeep

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.bkeep.databinding.FragmentSimulationBinding
import com.example.bkeep.simulation.simulate

class SimulationFragment : Fragment() {

    private var _binding: FragmentSimulationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimulationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val populations = listOf("Weak", "Medium", "Strong")
        val populationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, populations)
        populationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spPopulation.adapter = populationAdapter

        val weathers = listOf("Sunny", "Cloudy", "Light rain", "Moderate rain","Heavy rain")
        val weatherAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, weathers)
        weatherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spWeather.adapter = weatherAdapter

        binding.btnSimulate.setOnClickListener {
            runSimulation()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun runSimulation() {

        val humMin = binding.etHumidityMin.text.toString().toFloatOrNull()
        val humMax = binding.etHumidityMax.text.toString().toFloatOrNull()

        val tempMin = binding.etTempMin.text.toString().toFloatOrNull()
        val tempMax = binding.etTempMax.text.toString().toFloatOrNull()

        val luxMin = binding.etBrightnessMin.text.toString().toFloatOrNull()
        val luxMax = binding.etBrightnessMax.text.toString().toFloatOrNull()

        val startWeight = binding.etWeight.text.toString().toFloatOrNull()
        val days = binding.etDuration.text.toString().toIntOrNull()

        val population = binding.spPopulation.selectedItem.toString()
        val weather = binding.spWeather.selectedItem.toString()

        // --- BASIC VALIDATION ---
        if (
            humMin == null || humMax == null ||
            tempMin == null || tempMax == null ||
            luxMin == null || luxMax == null ||
            startWeight == null || days == null
        ) {
            showError("Please fill in all fields")
            return
        }

        if (humMin >= humMax || tempMin >= tempMax || luxMin >= luxMax) {
            showError("Min values must be smaller than max values")
            return
        }

        // --- RUN SIMULATION ---
        val results = simulate(
            days = days,
            tempRange = tempMin..tempMax,
            humRange = humMin..humMax,
            luxRange = luxMin..luxMax,
            weather = weather,
            population = population
        )

        // --- CALCULATE FINAL WEIGHT ---
        val totalChange = results.sum()
        val finalWeight = startWeight + totalChange

        showResult(finalWeight, totalChange)
    }
    private fun showError(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_LONG).show()
    }

    private fun showResult(finalWeight: Float, totalChange: Float) {
        val message =
            "Simulation finished\n\n" +
                    "Total honey change: ${"%.2f".format(totalChange)} kg\n" +
                    "Final hive weight: ${"%.2f".format(finalWeight)} kg"

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Simulation Results")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }


}
package com.example.bkeep.ui.hive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.bkeep.databinding.FragmentHiveDetailBinding
import com.example.bkeep.network.RetrofitInstance
import com.example.lib.data.hive.Weight
import kotlinx.coroutines.launch
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HiveDetailFragment : Fragment() {

    private var _binding: FragmentHiveDetailBinding? = null
    private val binding get() = _binding!!

    private var hiveId = -1
    private var currentStatus = "offline"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hiveId = requireArguments().getInt("HIVE_ID")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHiveDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        loadHive()
        loadWeight()
        setupToggle()
    }

    private fun loadHive() {
        lifecycleScope.launch {
            val res = RetrofitInstance.hiveApi.getHiveById(hiveId)
            if (res.isSuccessful) {
                val list = res.body() ?: emptyList()
                if (list.isNotEmpty()) {
                    val hive = list[0]
                    binding.tvName.text = hive.name
                    binding.tvLocation.text = "Lokacija: ${hive.location}"
                    binding.tvType.text = "Tip: ${hive.type}"
                    binding.tvStatus.text = hive.status

                    currentStatus = hive.status
                    binding.switchStatus.isChecked = hive.status == "online"
                }
            }
        }
    }

    private fun setupToggle() {
        binding.switchStatus.setOnCheckedChangeListener { _, checked ->
            val newStatus = if (checked) "online" else "offline"
            binding.tvStatus.text = newStatus

            lifecycleScope.launch {
                try {
                    RetrofitInstance.hiveApi.updateHiveStatus(
                        mapOf(
                            "id" to hiveId.toString(),
                            "status" to newStatus
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadWeight() {
        lifecycleScope.launch {
            val res = RetrofitInstance.weightApi.getHiveWeight(hiveId)
            if (res.isSuccessful) {
                val list = res.body() ?: emptyList()

                if (list.isNotEmpty()) {
                    binding.tvWeight.text = "Teža: ${list.last().weight} kg"
                    showWeightChart(list)
                } else {
                    binding.tvWeight.text = "Teža: 0 kg"
                }
            }
        }
    }
    private fun showWeightChart(weights: List<Weight>) {
        val entries = weights.mapIndexed { index, w ->
            Entry(index.toFloat(), w.weight)
        }

        val dataSet = LineDataSet(entries, "Teža panja (kg)").apply {
            setDrawValues(false)
            setDrawCircles(true)
            lineWidth = 2f
            circleRadius = 4f
        }

        val lineData = LineData(dataSet)

        // parser za datum
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        binding.weightChart.apply {
            data = lineData
            description.isEnabled = false
            axisRight.isEnabled = false
            xAxis.granularity = 1f

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    val raw = weights.getOrNull(index)?.time_weight?.substring(0,10) ?: return ""
                    return try {
                        val date = inputFormat.parse(raw)
                        outputFormat.format(date)
                    } catch (e: Exception) {
                        ""
                    }
                }
            }
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

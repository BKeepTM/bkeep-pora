package com.example.bkeep.ui.hive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.bkeep.databinding.FragmentHiveDetailBinding
import com.example.bkeep.network.RetrofitInstance
import kotlinx.coroutines.launch

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
                RetrofitInstance.hiveApi.updateHive(
                    hiveId,
                    mapOf("status" to newStatus)
                )
            }
        }
    }

    private fun loadWeight() {
        lifecycleScope.launch {
            val res = RetrofitInstance.weightApi.getHiveWeight(hiveId)
            if (res.isSuccessful) {
                val list = res.body() ?: emptyList()
                binding.tvWeight.text =
                    if (list.isNotEmpty())
                        "Teža: ${list.last().weight} kg"
                    else "Teža: 0 kg"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

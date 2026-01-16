package com.example.bkeep.ui.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.bkeep.R
import com.example.bkeep.databinding.FragmentAddHiveBinding
import com.example.bkeep.network.RetrofitInstance
import com.example.bkeep.ui.list.HiveFragment
import com.example.bkeep.ui.map.MapFragment
import com.example.bkeep.ui.map.MapMode
import com.example.lib.data.hive.CreateHiveRequest
import kotlinx.coroutines.launch

class AddHiveFragment : Fragment() {

    private var _binding: FragmentAddHiveBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddHiveViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddHiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener(
            "pick_location",
            viewLifecycleOwner
        ) { _, bundle ->
            val lat = bundle.getDouble("lat")
            val lng = bundle.getDouble("lng")

            // shrani v ViewModel
            viewModel.setLocation(lat, lng)
        }

        binding.btnPickLocation.setOnClickListener {
            val fragment = MapFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("MODE", MapMode.PICK)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentView, fragment)
                .addToBackStack(null)
                .commit()
        }

        // Opazuje izbrano lokacijo iz ViewModel
        viewModel.pickedLocation.observe(viewLifecycleOwner) { loc ->
            if (loc != null) {
                binding.etCoordinates.setText(
                    "Lat: ${loc.latitude}, Lng: ${loc.longitude}"
                )
            }
        }

        val types = listOf("AZ", "LR", "DB")
        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = typeAdapter

        val statuses = listOf("offline", "online")
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = statusAdapter


        binding.btnCreateHive.setOnClickListener {
            val loc = viewModel.pickedLocation.value
            if (loc == null || binding.etName.text.isBlank() || binding.etLocation.text.isBlank()) {
                Toast.makeText(requireContext(), "Izpolni vsa polja", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = CreateHiveRequest(
                name = binding.etName.text.toString(),
                location = binding.etLocation.text.toString(),
                type = binding.spinnerType.selectedItem.toString(),
                status = binding.spinnerStatus.selectedItem.toString(),
                latitude = loc.latitude,
                longitude = loc.longitude
            )

            lifecycleScope.launch {
                try {
                    val response = RetrofitInstance.hiveApi.createHive(request)
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Panj ustvarjen!", Toast.LENGTH_SHORT).show()

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentView, HiveFragment())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Toast.makeText(requireContext(), "Napaka: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Napaka povezave: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

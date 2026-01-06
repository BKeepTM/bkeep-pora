package com.example.bkeep.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bkeep.databinding.FragmentMapBinding
import com.example.bkeep.network.RetrofitInstance
import com.example.lib.data.location.HiveLocation
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Nastavi konfiguracijo
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", 0))

        // Nastavi zemljevid
        val map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // Začetna lokacija
        val startPoint = GeoPoint(46.1512, 14.9955)
        map.controller.setZoom(8.8)
        map.controller.setCenter(startPoint)

        loadHives()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadHives() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.hiveApi.getHiveLocations()

                if (response.isSuccessful) {
                    response.body()?.forEach { hive ->
                        addHiveMarker(hive)
                    }
                    binding.map.invalidate()
                }
            } catch (e: Exception) {
                Log.e("MapFragment", "Napaka pri nalaganju panjev", e)
            }
        }
    }

    private fun addHiveMarker(hive: HiveLocation) {
        val map = binding.map
        val point = GeoPoint(hive.latitude, hive.longitude)
        val marker = Marker(map)
        marker.position = point
        marker.title = hive.name
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.snippet = "Location: ${hive.location}"

        /*marker.setOnMarkerClickListener { clickedMarker, mapView ->
            mapView.controller.animateTo(point)
            mapView.controller.setZoom(16.5)
            false
        }*/
        map.overlays.add(marker)
    }
}
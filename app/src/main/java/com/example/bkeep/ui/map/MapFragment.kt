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
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

enum class MapMode {
    VIEW,
    PICK
}
class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapMode: MapMode

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapMode = arguments?.getSerializable("MODE") as? MapMode ?: MapMode.VIEW

        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", 0))

        val map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val startPoint = GeoPoint(46.1512, 14.9955)
        map.controller.setZoom(8.8)
        map.controller.setCenter(startPoint)

        if (mapMode == MapMode.VIEW) {
            loadHives()
        } else {
            enablePickMode()
        }
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
    private fun enablePickMode() {
        val map = binding.map

        Toast.makeText(requireContext(), "Klikni na mapo za izbiro lokacije", Toast.LENGTH_SHORT).show()

        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                if (p != null) {
                    // Shrani koordinate v fragment result
                    parentFragmentManager.setFragmentResult(
                        "pick_location",
                        Bundle().apply {
                            putDouble("lat", p.latitude)
                            putDouble("lng", p.longitude)
                        }
                    )

                    parentFragmentManager.popBackStack()
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                //dolgi klik ne naredi nič
                return false
            }
        }

        val overlayEvents = MapEventsOverlay(mapEventsReceiver)
        map.overlays.add(overlayEvents)
    }

}
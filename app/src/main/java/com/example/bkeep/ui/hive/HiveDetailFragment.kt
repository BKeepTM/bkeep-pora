package com.example.bkeep.ui.hive

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bkeep.R
import com.example.bkeep.databinding.FragmentHiveDetailBinding
import com.example.bkeep.network.RetrofitInstance
import com.example.bkeep.ui.note.MyNoteRecyclerViewAdapter
import com.example.lib.data.hive.Weight
import com.example.lib.data.notes.CreateNoteRequest
import com.example.lib.data.notes.Note
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HiveDetailFragment : Fragment() {

    private var _binding: FragmentHiveDetailBinding? = null
    private val binding get() = _binding!!

    private var hiveId = -1
    private var currentStatus = "offline"

    private lateinit var adapter: MyNoteRecyclerViewAdapter
    private val notesList: MutableList<Note> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hiveId = requireArguments().getInt("HIVE_ID", -1)
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
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        adapter = MyNoteRecyclerViewAdapter(notesList)
        binding.rvNotes.adapter = adapter
        binding.rvNotes.layoutManager = LinearLayoutManager(context)

        setupSwipeToDelete() // swipe za brisanje note

        loadHive()
        loadWeight()
        loadNotes()

        setupToggle()
        setupAddNote()
    }

    private fun loadHive() {
        lifecycleScope.launch {
            try {
                val res = RetrofitInstance.hiveApi.getHiveById(hiveId)
                if (res.isSuccessful) {
                    val list = res.body() ?: emptyList()
                    if (list.isNotEmpty()) {
                        val hive = list[0]
                        binding.tvName.text = hive.name
                        binding.tvLocation.text = "Location: ${hive.location}"
                        binding.tvType.text = "Type: ${hive.type}"
                        binding.tvStatus.text = hive.status

                        currentStatus = hive.status
                        binding.switchStatus.isChecked = hive.status == "online"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
            try {
                val res = RetrofitInstance.weightApi.getHiveWeight(hiveId)
                if (res.isSuccessful) {
                    val list = res.body() ?: emptyList()
                    if (list.isNotEmpty()) {
                        binding.tvWeight.text = "Weight: ${list.last().weight} kg"
                        showWeightChart(list)
                    } else {
                        binding.tvWeight.text = "Weight: 0 kg"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showWeightChart(weights: List<Weight>) {
        val entries = weights.mapIndexed { index, w -> Entry(index.toFloat(), w.weight) }

        val dataSet = LineDataSet(entries, "Hive weight (kg)").apply {
            setDrawValues(false)
            setDrawCircles(true)
            lineWidth = 2f
            circleRadius = 4f
        }

        val lineData = LineData(dataSet)

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

    private fun loadNotes() {
        lifecycleScope.launch {
            try {
                val res = RetrofitInstance.notesApi.getHiveNotes(hiveId)
                if (res.isSuccessful) {
                    val notes = res.body() ?: emptyList()
                    notesList.clear()
                    notesList.addAll(notes)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupAddNote() {
        binding.btnAddNote.setOnClickListener {
            val content = binding.etNote.text.toString()
            if (content.isBlank()) return@setOnClickListener

            lifecycleScope.launch {
                try {
                    val request = CreateNoteRequest(
                        content = content,
                        time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                        hiveId = hiveId
                    )
                    val res = RetrofitInstance.notesApi.createNote(request)
                    if (res.isSuccessful) {
                        binding.etNote.text.clear()
                        loadNotes()
                    } else {
                        Log.e("HiveDetailFragment", "Failed to create note: ${res.code()}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setupSwipeToDelete() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = adapter.getItem(position)
                adapter.removeItem(position)

                lifecycleScope.launch {
                    try {
                        val response = RetrofitInstance.notesApi.deleteNote(note.id)
                        if (!response.isSuccessful) {
                            adapter.addItem(position, note)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        adapter.addItem(position, note)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float,
                actionState: Int, isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val icon = ContextCompat.getDrawable(requireContext(), R.drawable.delete_hive) ?: return
                val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconBottom = iconTop + icon.intrinsicHeight

                if (dX < 0) { // swipe left
                    val iconRight = itemView.right - iconMargin
                    val iconLeft = iconRight - icon.intrinsicWidth
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    icon.draw(c)
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(binding.rvNotes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

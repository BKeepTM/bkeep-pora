package com.example.bkeep.ui.note

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
import com.example.bkeep.databinding.FragmentHiveBinding
import com.example.bkeep.databinding.FragmentHiveDetailBinding
import com.example.bkeep.databinding.FragmentNoteListBinding
import com.example.bkeep.network.RetrofitInstance
import com.example.lib.data.notes.Note
import kotlinx.coroutines.launch

class NoteFragment : Fragment() {

    private var _binding: FragmentHiveDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MyNoteRecyclerViewAdapter
    private val notesList = mutableListOf<Note>()

    private var hiveId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hiveId = requireArguments().getInt("HIVE_ID", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHiveDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadNotes()
        setupSwipeToDelete()
    }

    private fun setupRecyclerView() {
        adapter = MyNoteRecyclerViewAdapter(notesList)
        binding.rvNotes.adapter = adapter
        binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadNotes() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.notesApi.getHiveNotes(hiveId)
                if (response.isSuccessful) {
                    val notes = response.body() ?: emptyList()
                    notesList.clear()
                    notesList.addAll(notes)
                    adapter.notifyDataSetChanged() // osveži RecyclerView
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
                            // Če je request neuspešen, vrni nazaj
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
                    if (kotlin.math.abs(dX) > 100) {
                        icon.draw(c)
                    }
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

    companion object {
        fun newInstance(hiveId: Int): NoteFragment {
            val fragment = NoteFragment()
            val bundle = Bundle()
            bundle.putInt("HIVE_ID", hiveId)
            fragment.arguments = bundle
            return fragment
        }
    }
}

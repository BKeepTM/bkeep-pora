package com.example.bkeep.ui.list

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bkeep.R
import com.example.bkeep.network.RetrofitInstance
import com.example.bkeep.ui.hive.HiveDetailFragment
import com.example.lib.data.hive.Hive
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HiveFragment : Fragment() {

    private var columnCount = 1
    private lateinit var hiveAdapter: MyHiveRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        recyclerView = inflater.inflate(R.layout.fragment_hive_list, container, false) as RecyclerView

        hiveAdapter = MyHiveRecyclerViewAdapter(mutableListOf()) { hive ->
            openHiveDetail(hive.id)
        }

        recyclerView.layoutManager =
            if (columnCount <= 1)
                LinearLayoutManager(context)
            else
                GridLayoutManager(context, columnCount)

        recyclerView.adapter = hiveAdapter

        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadHives()
        setupSwipeToDelete()
    }

    private fun loadHives() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.hiveApi.getHiveByUserId()
                if (response.isSuccessful) {
                    response.body()?.let { hives ->
                        hiveAdapter.setData(hives)
                    }
                }
            } catch (e: Exception) {
                Log.e("HiveFragment", "Error loading hives", e)
            }
        }
    }

    private fun setupSwipeToDelete() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val hive = hiveAdapter.getItem(position)

                hiveAdapter.removeItem(position)

                // Send deletion request to backend
                lifecycleScope.launch {
                    try {
                        val response = RetrofitInstance.hiveApi.deleteHive(hive.id)
                        if (!response.isSuccessful) {
                            Log.e("HiveFragment", "Failed to delete hive: ${response.code()}")
                            // Restore item if deletion failed
                            hiveAdapter.addItem(position, hive)
                        }
                    } catch (e: Exception) {
                        Log.e("HiveFragment", "Error deleting hive", e)
                        hiveAdapter.addItem(position, hive)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
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
                    if (kotlin.math.abs(dX) > 100) { // draw icon after some threshold
                        icon.draw(c)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(recyclerView)
    }

    private fun openHiveDetail(hiveId: Int) {
        val fragment = HiveDetailFragment().apply {
            arguments = Bundle().apply {
                putInt("HIVE_ID", hiveId)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentView, fragment)
            .addToBackStack(null)
            .commit()
    }

}

package com.example.bkeep.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bkeep.ui.list.MyHiveRecyclerViewAdapter
import com.example.bkeep.R
import com.example.bkeep.network.RetrofitInstance
import com.example.bkeep.placeholder.PlaceholderContent
import com.example.lib.data.hive.Hive
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class HiveFragment : Fragment() {

    private var columnCount = 1
    private lateinit var hiveAdapter: MyHiveRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val recyclerView =
            inflater.inflate(R.layout.fragment_hive_list, container, false) as RecyclerView

        hiveAdapter = MyHiveRecyclerViewAdapter(mutableListOf())

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
                Log.e("HiveFragment", "Napaka pri nalaganju panjev", e)
            }
        }
    }
}
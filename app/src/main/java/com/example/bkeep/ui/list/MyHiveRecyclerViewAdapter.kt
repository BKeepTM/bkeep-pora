package com.example.bkeep.ui.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bkeep.databinding.FragmentHiveBinding
import com.example.bkeep.placeholder.PlaceholderContent
import com.example.lib.data.hive.Hive

/**
 * [androidx.recyclerview.widget.RecyclerView.Adapter] that can display a [com.example.bkeep.placeholder.PlaceholderContent.PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyHiveRecyclerViewAdapter(
    private val values: MutableList<Hive>
) : RecyclerView.Adapter<MyHiveRecyclerViewAdapter.ViewHolder>() {

    fun setData(newHives: List<Hive>) {
        values.clear()
        values.addAll(newHives)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentHiveBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.hiveName.text = item.name
        holder.hiveLocation.text = item.location
        holder.hiveType.text = item.type
        holder.hiveStatus.text = item.status

        if(item.status == "online"){
            holder.hiveStatus.text = item.status
            holder.hiveStatus.setTextColor(Color.parseColor("#f5426f"));
        }else if(item.status == "offline"){
            holder.hiveStatus.text = item.status
            holder.hiveStatus.setTextColor(Color.parseColor("#19b34c"));
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentHiveBinding) : RecyclerView.ViewHolder(binding.root) {
        val hiveName: TextView = binding.hiveName
        val hiveLocation: TextView = binding.hiveLocation
        val hiveType : TextView = binding.hiveType
        val hiveStatus : TextView = binding.hiveStatus
    }

}
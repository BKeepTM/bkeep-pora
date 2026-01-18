package com.example.bkeep.ui.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bkeep.databinding.FragmentNoteBinding
import com.example.lib.data.hive.Hive
import com.example.lib.data.notes.Note
import java.text.SimpleDateFormat
import java.util.Locale

class MyNoteRecyclerViewAdapter(
    private val notes: MutableList<Note>
) : RecyclerView.Adapter<MyNoteRecyclerViewAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding: FragmentNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = FragmentNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.binding.tvContent.text = note.content ?: ""
        holder.binding.tvTime.text = formatDate(note.time)
    }

    override fun getItemCount(): Int = notes.size

    fun getItem(position: Int): Note = notes[position]

    fun removeItem(position: Int) {
        notes.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(position: Int, note: Note) {
        notes.add(position, note)
        notifyItemInserted(position)
    }

    private fun formatDate(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        return try {
            // Podpiramo format "yyyy-MM-dd HH:mm:ss" ali ISO8601 "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val parsedDate = when {
                raw.contains("T") -> {
                    // ISO format
                    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    isoFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                    isoFormat.parse(raw)
                }
                else -> {
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(raw)
                }
            }
            val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            outputFormat.format(parsedDate!!)
        } catch (e: Exception) {
            raw
        }
    }
}

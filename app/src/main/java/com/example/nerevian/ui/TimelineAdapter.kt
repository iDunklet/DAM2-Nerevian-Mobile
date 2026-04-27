package com.example.nerevian.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.example.nerevian.core.model.operations.TimelineEvent

class TimelineAdapter(private val events: List<TimelineEvent>) :
    RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    class TimelineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvEventTitle)
        val tvDate: TextView = view.findViewById(R.id.tvEventDate)
        val line: View = view.findViewById(R.id.viewLine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timeline, parent, false)
        return TimelineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val event = events[position]
        holder.tvTitle.text = event.evento
        holder.tvDate.text = event.fecha

        // Ocultar la línea en el último elemento
        holder.line.visibility = if (position == events.size - 1) View.GONE else View.VISIBLE
    }

    override fun getItemCount() = events.size
}
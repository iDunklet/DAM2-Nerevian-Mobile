package com.example.nerevian.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.example.nerevian.core.model.incoterms.TrackingStatus

class TrackingStatusAdapter(
    private var statusList: List<TrackingStatus>
) : RecyclerView.Adapter<TrackingStatusAdapter.StatusViewHolder>() {

    class StatusViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lineTop: View = view.findViewById(R.id.lineTop)
        val lineBottom: View = view.findViewById(R.id.lineBottom)
        val dotStatus: ImageView = view.findViewById(R.id.dotStatus)
        val tvStatusTitle: TextView = view.findViewById(R.id.tvStatusTitle)
        val tvStatusTime: TextView = view.findViewById(R.id.tvStatusTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tracking_status, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val status = statusList[position]


        holder.tvStatusTitle.text = status.title
        holder.tvStatusTime.text = status.time


        if (position == 0) {
            holder.lineTop.visibility = View.INVISIBLE
        } else {
            holder.lineTop.visibility = View.VISIBLE
        }

        if (position == statusList.size - 1) {
            holder.lineBottom.visibility = View.INVISIBLE
        } else {
            holder.lineBottom.visibility = View.VISIBLE
        }

        if (status.isCompleted) {
            holder.dotStatus.setBackgroundResource(R.drawable.shape_timeline_dot_active)
            holder.tvStatusTitle.setTextColor(Color.parseColor("#000000"))
            holder.lineTop.setBackgroundColor(Color.parseColor("#2B6B78"))
            holder.lineBottom.setBackgroundColor(Color.parseColor("#2B6B78"))
        } else {

            holder.dotStatus.setBackgroundResource(R.drawable.shape_timeline_dot_active)
            holder.tvStatusTitle.setTextColor(Color.parseColor("#888888"))
            holder.lineTop.setBackgroundColor(Color.parseColor("#DDDDDD"))
            holder.lineBottom.setBackgroundColor(Color.parseColor("#DDDDDD"))

        }
    }

    override fun getItemCount(): Int = statusList.size

    fun updateData(newList: List<TrackingStatus>) {
        statusList = newList
        notifyDataSetChanged()
    }
}
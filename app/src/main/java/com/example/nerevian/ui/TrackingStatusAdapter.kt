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

        // 1. 填入文字数据
        holder.tvStatusTitle.text = status.title
        holder.tvStatusTime.text = status.time

        // 🌟 2. 时间轴魔法：隐藏多余的线条
        // 如果是第一条，隐藏上半截线
        if (position == 0) {
            holder.lineTop.visibility = View.INVISIBLE
        } else {
            holder.lineTop.visibility = View.VISIBLE
        }

        // 如果是最后一条，隐藏下半截线
        if (position == statusList.size - 1) {
            holder.lineBottom.visibility = View.INVISIBLE
        } else {
            holder.lineBottom.visibility = View.VISIBLE
        }

        // 3. 根据是否完成 (isCompleted) 改变颜色
        if (status.isCompleted) {
            // 已完成：主色调 (深青色)
            holder.dotStatus.setBackgroundResource(R.drawable.shape_timeline_dot_active)
            holder.tvStatusTitle.setTextColor(Color.parseColor("#000000"))
            holder.lineTop.setBackgroundColor(Color.parseColor("#2B6B78"))
            holder.lineBottom.setBackgroundColor(Color.parseColor("#2B6B78"))
        } else {
            // 未完成：灰色
            // 注意：如果你之前没建灰色的 drawable，这里可能暂时没有圆点，你可以先用 active 替代，或者自己建一个灰色的 shape
            holder.dotStatus.setBackgroundResource(R.drawable.shape_timeline_dot_active)
            holder.tvStatusTitle.setTextColor(Color.parseColor("#888888"))
            holder.lineTop.setBackgroundColor(Color.parseColor("#DDDDDD"))
            holder.lineBottom.setBackgroundColor(Color.parseColor("#DDDDDD"))
            // 灰色的圆点可以这么写：holder.dotStatus.imageTintList = ColorStateList.valueOf(Color.parseColor("#DDDDDD"))
        }
    }

    override fun getItemCount(): Int = statusList.size

    // 这个方法留给未来：当用户点击了上面的订单卡片，我们要把新的状态列表传进来刷新
    fun updateData(newList: List<TrackingStatus>) {
        statusList = newList
        notifyDataSetChanged()
    }
}
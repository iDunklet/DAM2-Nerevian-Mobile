package com.example.nerevian.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.example.nerevian.core.model.incoterms.TrackingOrder
import com.google.android.material.card.MaterialCardView

class TrackingOrderAdapter(
    private val orderList: List<TrackingOrder>,
    private val onOrderSelected: (TrackingOrder) -> Unit
) : RecyclerView.Adapter<TrackingOrderAdapter.OrderViewHolder>() {


    private var selectedPosition = 0

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardOrder: MaterialCardView = view.findViewById(R.id.cardOrder)
        val tvReferenceCode: TextView = view.findViewById(R.id.tvReferenceCode)
        val tvRoute: TextView = view.findViewById(R.id.tvRoute)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tracking_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.tvReferenceCode.text = order.referenceCode

        holder.tvRoute.text = order.route

        if (position == selectedPosition) {

            holder.cardOrder.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.tvReferenceCode.setTextColor(Color.parseColor("#000000"))
            holder.tvRoute.setTextColor(Color.parseColor("#888888"))
        } else {

            holder.cardOrder.setCardBackgroundColor(Color.parseColor("#215A68"))
            holder.tvReferenceCode.setTextColor(Color.parseColor("#FFFFFF"))
            holder.tvRoute.setTextColor(Color.parseColor("#CCCCCC"))
        }

        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == RecyclerView.NO_POSITION || currentPosition == selectedPosition) return@setOnClickListener

            val previousPosition = selectedPosition
            selectedPosition = currentPosition

            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            onOrderSelected(orderList[currentPosition])
        }
    }

    override fun getItemCount(): Int = orderList.size
}
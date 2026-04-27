package com.example.nerevian.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.example.nerevian.core.model.operations.OrderListItem
import com.google.android.material.card.MaterialCardView

class OrderSelectorAdapter(
    private val orders: List<OrderListItem>,
    private val onOrderSelected: (Int) -> Unit
) : RecyclerView.Adapter<OrderSelectorAdapter.OrderViewHolder>() {

    private var selectedPosition = 0

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.cardOrder) // Asegúrate de que el CardView en item_order_selector tenga este ID
        val tvRef: TextView = view.findViewById(R.id.tvOrderRef)
        val tvRoute: TextView = view.findViewById(R.id.tvOrderRoute)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_selector, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvRef.text = order.referenceCode
        holder.tvRoute.text = order.ruta ?: "---"

        // Cambiar opacidad si está seleccionado
        holder.card.alpha = if (selectedPosition == position) 1.0f else 0.6f

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onOrderSelected(order.id)
        }
    }

    override fun getItemCount() = orders.size
}
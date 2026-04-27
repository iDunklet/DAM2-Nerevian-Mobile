package com.example.nerevian.ui.agent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.example.nerevian.core.model.operations.OrderListItem
import com.google.android.material.button.MaterialButton

class OrderAdapter(
    private var orders: List<OrderListItem>,
    // Opcional: Pasamos funciones lambda para manejar los clicks en la Activity/Fragment
    private val onDetailsClick: (Int) -> Unit = {},
    private val onUpdateClick: (Int) -> Unit = {}
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderRef: TextView = view.findViewById(R.id.tvOrderRef)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val tvClientName: TextView = view.findViewById(R.id.tvClientName)
        val tvOrigin: TextView = view.findViewById(R.id.tvOrigin)
        val tvDestination: TextView = view.findViewById(R.id.tvDestination)
        val tvDate: TextView = view.findViewById(R.id.tvDate)

        // Botones
        val btnDetalles: MaterialButton = view.findViewById(R.id.btnDetalles)
        val btnActualizar: MaterialButton = view.findViewById(R.id.btnActualizar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        // 1. Textos directos
        holder.tvOrderRef.text = order.referenceCode
        holder.tvOrderStatus.text = order.status
        holder.tvClientName.text = order.clientName

        // 2. Puertos (Si superan un tamaño podrías hacerles un .take(3) para que sean siglas)
        holder.tvOrigin.text = order.originPort
        holder.tvDestination.text = order.destinationPort

        // 3. Fecha (Formateamos por si viene nula o muy larga desde el backend)
        val dateText = if (!order.eta.isNullOrEmpty() && order.eta.length >= 10) {
            "ETA: ${order.eta.substring(0, 10)}"
        } else {
            "ETA: Pdte."
        }
        holder.tvDate.text = dateText

        // 4. Listeners de los botones
        holder.btnDetalles.setOnClickListener {
            onDetailsClick(order.id)
        }

        holder.btnActualizar.setOnClickListener {
            onUpdateClick(order.id)
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<OrderListItem>) {
        this.orders = newOrders
        notifyDataSetChanged() // Refresca la lista completa
    }
}
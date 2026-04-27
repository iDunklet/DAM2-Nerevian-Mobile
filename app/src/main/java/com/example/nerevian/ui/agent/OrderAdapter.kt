package com.example.nerevian.ui.agent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.example.nerevian.core.model.operations.OrderListItem
import com.google.android.material.button.MaterialButton

/**
 * Adaptador de pedidos para RecyclerView.
 * Modularización: podría moverse a módulo 'order' y usar DiffUtil para mejor rendimiento.
 *
 * @param orders Lista de pedidos.
 * @param onDetailsClick Callback al pulsar "Detalles" (recibe orderId).
 * @param onUpdateClick Callback al pulsar "Actualizar" (recibe orderId).
 */
class OrderAdapter(
    private var orders: List<OrderListItem>,
    private val onDetailsClick: (Int) -> Unit = {},
    private val onUpdateClick: (Int) -> Unit = {}
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    /** ViewHolder con referencias a vistas del layout item_order */
    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderRef: TextView = view.findViewById(R.id.tvOrderRef)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val tvClientName: TextView = view.findViewById(R.id.tvClientName)
        val tvOrigin: TextView = view.findViewById(R.id.tvOrigin)
        val tvDestination: TextView = view.findViewById(R.id.tvDestination)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
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

        holder.tvOrderRef.text = order.referenceCode
        holder.tvOrderStatus.text = order.status
        holder.tvClientName.text = order.clientName
        holder.tvOrigin.text = order.originPort
        holder.tvDestination.text = order.destinationPort

        // Formato básico de fecha ETA
        val dateText = if (!order.eta.isNullOrEmpty() && order.eta.length >= 10) {
            "ETA: ${order.eta.substring(0, 10)}"
        } else {
            "ETA: Pdte."
        }
        holder.tvDate.text = dateText

        // Los callbacks desacoplan la lógica del adaptador
        holder.btnDetalles.setOnClickListener { onDetailsClick(order.id) }
        holder.btnActualizar.setOnClickListener { onUpdateClick(order.id) }
    }

    override fun getItemCount(): Int = orders.size

    /** Actualiza los datos y refresca la vista completa */
    fun updateData(newOrders: List<OrderListItem>) {
        this.orders = newOrders
        notifyDataSetChanged()
    }
}
package com.example.nerevian.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R


data class AgentePedido(
    val id: String,
    val client: String,
    val status: String,
    val route: String,
    val timeInfo: String
)

class PedidosAdapter(private val items: List<AgentePedido>) :
    RecyclerView.Adapter<PedidosAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvStatusBadge: TextView = view.findViewById(R.id.tvStatusBadge)
        val tvClientName: TextView = view.findViewById(R.id.tvClientName)
        val tvRoute: TextView = view.findViewById(R.id.tvRoute)
        val tvTimeInfo: TextView = view.findViewById(R.id.tvTimeInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvOrderId.text = item.id
        holder.tvClientName.text = item.client
        holder.tvStatusBadge.text = item.status
        holder.tvRoute.text = item.route
        holder.tvTimeInfo.text = item.timeInfo

        val badgeBackground = GradientDrawable()
        badgeBackground.cornerRadius = 30f

        when (item.status) {
            "En Tránsito" -> {
                holder.tvStatusBadge.setTextColor(Color.parseColor("#1976D2"))
                badgeBackground.setColor(Color.parseColor("#E3F2FD"))
            }
            "En Aduana" -> {
                holder.tvStatusBadge.setTextColor(Color.parseColor("#D32F2F"))
                badgeBackground.setColor(Color.parseColor("#FFEBEE"))
            }
            "En Preparación" -> {
                holder.tvStatusBadge.setTextColor(Color.parseColor("#F57C00"))
                badgeBackground.setColor(Color.parseColor("#FFF3E0"))
            }
            "Entregado" -> {
                holder.tvStatusBadge.setTextColor(Color.parseColor("#388E3C"))
                badgeBackground.setColor(Color.parseColor("#E8F5E9"))
            }
            else -> {
                holder.tvStatusBadge.setTextColor(Color.GRAY)
                badgeBackground.setColor(Color.LTGRAY)
            }
        }
        holder.tvStatusBadge.background = badgeBackground
    }

    override fun getItemCount() = items.size
}
package com.example.nerevian.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.example.nerevian.core.model.business.presupuesto.Presupuesto

class PresupuestoAdapter(private val presupuestos: List<Presupuesto>) :
    RecyclerView.Adapter<PresupuestoAdapter.PresupuestoViewHolder>() {

    class PresupuestoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvId)
        val tvOrigen: TextView = view.findViewById(R.id.tvOrigen)
        val tvDestino: TextView = view.findViewById(R.id.tvDestino)
        val tvTipo: TextView = view.findViewById(R.id.tvTipo)
        val tvExpira: TextView = view.findViewById(R.id.tvExpira)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        val tvIncoterm: TextView = view.findViewById(R.id.tvIncoterm)
        val tvDetalle: TextView = view.findViewById(R.id.tvDetalle)

        val ivArrow: ImageView = view.findViewById(R.id.ivArrow)
        val layoutDetails: LinearLayout = view.findViewById(R.id.layoutDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresupuestoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_presupuesto, parent, false)
        return PresupuestoViewHolder(view)
    }

    override fun getItemCount() = presupuestos.size

    override fun onBindViewHolder(holder: PresupuestoViewHolder, position: Int) {
        val presupuesto = presupuestos[position] // 拿到当前这一行的具体数据

        holder.tvId.text = presupuesto.id
        holder.tvOrigen.text = presupuesto.origen
        holder.tvDestino.text = presupuesto.destino
        holder.tvTipo.text = presupuesto.tipo
        holder.tvExpira.text = presupuesto.expira
        holder.tvPrecio.text = presupuesto.precio
        holder.tvIncoterm.text = presupuesto.incoterm
        holder.tvDetalle.text = presupuesto.detalle
        holder.layoutDetails.visibility = if (presupuesto.isExpanded) View.VISIBLE else View.GONE
        holder.ivArrow.rotation = if (presupuesto.isExpanded) 180f else 0f


        holder.ivArrow.setOnClickListener {
            presupuesto.isExpanded = !presupuesto.isExpanded

            if (presupuesto.isExpanded) {
                holder.layoutDetails.visibility = View.VISIBLE
                holder.ivArrow.animate().rotation(180f).setDuration(200).start()
            } else {
                holder.layoutDetails.visibility = View.GONE
                holder.ivArrow.animate().rotation(0f).setDuration(200).start()
            }
        }
    }
}
package com.example.nerevian.ui.client

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.example.nerevian.core.model.business.budget.Budget
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText

class PresupuestoAdapter(private val presupuestos: List<Budget>) :
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
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val layoutBotones: LinearLayout = view.findViewById(R.id.layoutBotones)

        val btnRechazar: View = view.findViewById(R.id.btnRechazar)
        val btnAceptar: View = view.findViewById(R.id.btnAceptar)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresupuestoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_presupuesto, parent, false)
        return PresupuestoViewHolder(view)
    }

    override fun getItemCount() = presupuestos.size

    override fun onBindViewHolder(holder: PresupuestoViewHolder, position: Int) {
        val presupuesto = presupuestos[position]

        holder.tvId.text = presupuesto.id
        holder.tvOrigen.text = presupuesto.origen
        holder.tvDestino.text = presupuesto.destino
        holder.tvTipo.text = presupuesto.tipo
        holder.tvExpira.text = presupuesto.expira
        holder.tvPrecio.text = presupuesto.precio
        holder.tvIncoterm.text = presupuesto.incoterm
        holder.tvDetalle.text = presupuesto.detalle

        // cambiar color segun del estado
        holder.tvEstado.text = presupuesto.estado
        when (presupuesto.estado) {
            "Pendiente" -> {
                holder.tvEstado.setTextColor(Color.parseColor("#E65100"))
                holder.tvEstado.setBackgroundColor(Color.parseColor("#FFF3E0"))
                holder.layoutBotones.visibility = View.VISIBLE
            }
            "Aceptado" -> {
                holder.tvEstado.setTextColor(Color.parseColor("#0BA360"))
                holder.tvEstado.setBackgroundColor(Color.parseColor("#E8F6F0"))
                holder.layoutBotones.visibility = View.GONE
            }
            "Rechazado" -> {
                holder.tvEstado.setTextColor(Color.parseColor("#666666"))
                holder.tvEstado.setBackgroundColor(Color.parseColor("#F0F0F0"))
                holder.layoutBotones.visibility = View.GONE
            }
        }

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
        holder.btnAceptar.setOnClickListener {
            val dialog = BottomSheetDialog(holder.itemView.context)
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.layout_dialog_aceptar, null)

            val btnConfirmar = dialogView.findViewById<View>(R.id.btnConfirmarAceptar)

            btnConfirmar.setOnClickListener {
                presupuesto.estado = "Aceptado"

                notifyItemChanged(position)

                dialog.dismiss()
            }

            dialog.setContentView(dialogView)
            dialog.show()
        }

        holder.btnRechazar.setOnClickListener {
            val dialog = BottomSheetDialog(holder.itemView.context)

            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.layout_dialog_rechazar, null)

            val btnConfirmar = dialogView.findViewById<View>(R.id.btnConfirmarRechazo)
            val etMotivo = dialogView.findViewById<TextInputEditText>(R.id.etMotivo)

            btnConfirmar.setOnClickListener {
                val motivo = etMotivo.text.toString()

                if (motivo.isNotEmpty()) {
                    presupuesto.estado = "Rechazado"

                    notifyItemChanged(position)

                    dialog.dismiss()
                } else {

                    etMotivo.error = "Este campo es obligatorio"
                }
            }
            dialog.setContentView(dialogView)
            dialog.show()
        }
    }
}
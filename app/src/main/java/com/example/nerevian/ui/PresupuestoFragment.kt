package com.example.nerevian.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.example.nerevian.core.model.business.presupuesto.Presupuesto

class PresupuestoFragment : Fragment(R.layout.fragment_presupuesto) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //  listaDatos
        val listaDatos = listOf(
            Presupuesto("COT-045", "Shanghai", "Valencia", "FCL Marítimo", "Expira: 30 Oct", "4.500 €", "FOB", "Incluye flete marítimo, THC origen y gastos de documentación. Tránsito estimado: 32 días.", "Pendiente"), 
            Presupuesto("COT-046", "Valencia", "New York", "Aéreo Urgente", "Expira: 28 Oct", "2.850 €", "EXW", "Incluye flete aéreo y gastos en origen.", "Pendiente"),
            Presupuesto("COT-042", "Castellón", "Dubai", "LCL Marítimo", "Aceptado: 19 Oct", "3.100 €", "CIF", "Incluye seguro y flete hasta puerto.", "Aceptado"),
            Presupuesto("COT-039", "Bilbao", "Rotterdam", "Terrestre FTL", "Rechazado: 10 Oct", "1.200 €", "DAP", "Entrega directa en almacén.", "Rechazado")
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = PresupuestoAdapter(listaDatos)
        recyclerView.adapter = adapter
    }
}
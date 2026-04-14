package com.example.nerevian.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R

class PedidosFragment : Fragment(R.layout.fragment_agente_pedidos) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvAgentePedidos = view.findViewById<RecyclerView>(R.id.rvAgentePedidos)
        rvAgentePedidos.layoutManager = LinearLayoutManager(requireContext())


        val mockData = listOf(
            AgentePedido("EXP-8821", "Techimport SL", "En Tránsito", "SHA -> VLC", "ETA: 12 Oct"),
            AgentePedido("EXP-9923", "Muebles García", "En Aduana", "VLC -> NYC", "Retenido"),
            AgentePedido("EXP-7732", "AgroExport", "En Preparación", "BOM -> ALG", "Salida: 05 Nov"),
            AgentePedido("EXP-6610", "Inditex", "Entregado", "RTM -> BCN", "15 Sep")
        )

        val adapter = PedidosAdapter(mockData)
        rvAgentePedidos.adapter = adapter
    }
}
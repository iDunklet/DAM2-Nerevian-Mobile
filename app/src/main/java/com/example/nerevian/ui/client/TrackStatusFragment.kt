package com.example.nerevian.ui.client

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.example.nerevian.core.model.operations.OrderDetailResponse
import com.example.nerevian.data.network.RetrofitInstance
import com.example.nerevian.ui.OrderSelectorAdapter
import com.example.nerevian.ui.TimelineAdapter
import kotlinx.coroutines.launch

class TrackStatusFragment : Fragment() {

    private lateinit var rvOrderSelector: RecyclerView
    private lateinit var rvTimeline: RecyclerView
    private lateinit var tvEtaDate: TextView
    private lateinit var tvStatusBadge: TextView
    private lateinit var tvOriginPort: TextView
    private lateinit var tvDestinationPort: TextView
    private lateinit var tvContainerId: TextView
    private lateinit var tvWeight: TextView
    private lateinit var tvVolume: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_track_status, container, false)

        rvOrderSelector = view.findViewById(R.id.rvOrderSelector)
        rvTimeline = view.findViewById(R.id.rvTimeline)
        tvEtaDate = view.findViewById(R.id.tvEtaDate)
        tvStatusBadge = view.findViewById(R.id.tvStatusBadge)
        tvOriginPort = view.findViewById(R.id.tvOriginPort)
        tvDestinationPort = view.findViewById(R.id.tvDestinationPort)
        tvContainerId = view.findViewById(R.id.tvContainerId)
        tvWeight = view.findViewById(R.id.tvWeight)
        tvVolume = view.findViewById(R.id.tvVolume)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvOrderSelector.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvTimeline.layoutManager = LinearLayoutManager(requireContext())
        rvTimeline.isNestedScrollingEnabled = false

        loadAllOrders()
    }

    private fun loadAllOrders() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getAllTracks()
                if (response.isSuccessful && response.body() != null) {
                    val orders = response.body()!!
                    rvOrderSelector.adapter = OrderSelectorAdapter(orders) { selectedOrderId ->
                        fetchOrderDetails(selectedOrderId)
                    }
                    if (orders.isNotEmpty()) fetchOrderDetails(orders[0].id)
                }
            } catch (e: Exception) {
                Log.e("NEREVIAN_DEBUG", "Error loadAllOrders: ${e.message}")
            }
        }
    }

    private fun fetchOrderDetails(id: Int) {
        lifecycleScope.launch {
            try {
                Log.d("NEREVIAN_DEBUG", "Intentando traer ID: $id")
                val response = RetrofitInstance.api.getOrderDetails(id)

                if (response.isSuccessful && response.body() != null) {
                    updateUI(response.body()!!)
                } else {
                    Log.e("NEREVIAN_DEBUG", "Error ${response.code()}: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("NEREVIAN_DEBUG", "Excepción: ${e.message}")
            }
        }
    }

    // --- AQUÍ ESTÁ LA FUNCIÓN QUE FALTABA ---
    private fun updateUI(data: OrderDetailResponse) {
        // Usamos los nombres de variables que definimos en la Data Class
        tvEtaDate.text = data.historial.firstOrNull()?.fecha ?: "Pendiente"
        tvStatusBadge.text = data.estadoNombre
        tvOriginPort.text = data.clienteNombre // puerto_origen
        tvDestinationPort.text = data.destinoNombre // puerto_destino
        tvContainerId.text = data.numContenedor ?: "S/N"
        tvWeight.text = data.peso ?: "0 kg"
        tvVolume.text = data.volumen ?: "0 m³"

        // Actualizamos el RecyclerView del historial
        rvTimeline.adapter = TimelineAdapter(data.historial)

        Log.d("NEREVIAN_DEBUG", "UI pintada con éxito para la referencia: ${data.referencia}")
    }
}
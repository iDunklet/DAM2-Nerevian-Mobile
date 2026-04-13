package com.example.nerevian.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.example.nerevian.core.model.incoterms.TrackingOrder
import com.example.nerevian.core.model.incoterms.TrackingStatus
import com.example.nerevian.data.network.TrackingApiService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private lateinit var timelineAdapter: TrackingStatusAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvShipments = view.findViewById<RecyclerView>(R.id.rvShipments)
        val rvTimeline = view.findViewById<RecyclerView>(R.id.rvTimeline)
        val tvEtaDate = view.findViewById<TextView>(R.id.tvEtaDate)
        val tvGlobalStatus = view.findViewById<TextView>(R.id.tvGlobalStatus)
        val tvContainerNumber = view.findViewById<TextView>(R.id.tvContainerNumber)

        rvShipments.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvTimeline.layoutManager = LinearLayoutManager(requireContext())

        // Retrofit
        val myOrderList = listOf(
            TrackingOrder("EXP-8821", "SHA -> VLC", "13 Oct 2023", "En Tránsito", "MSKU9988771"),
            TrackingOrder("EXP-8822", "NGB -> BCN", "16 Oct 2023", "En Aduanas", "CMAU7766552"),
            TrackingOrder("EXP-8823", "QIN -> MAD", "20 Oct 2023", "Pendiente", "MEDU3344553")
        )

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5125/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(TrackingApiService::class.java)

        timelineAdapter = TrackingStatusAdapter(emptyList<TrackingStatus>())
        rvTimeline.adapter = timelineAdapter

        updateTrackingDetails("EXP-8821", tvEtaDate, tvGlobalStatus, tvContainerNumber, apiService)

        val orderAdapter = TrackingOrderAdapter(myOrderList) { selectedOrder ->
            updateTrackingDetails(selectedOrder.referenceCode, tvEtaDate, tvGlobalStatus, tvContainerNumber, apiService)
        }
        rvShipments.adapter = orderAdapter
    }

    private fun updateTrackingDetails(
        orderId: String,
        tvEtaDate: TextView,
        tvGlobalStatus: TextView,
        tvContainerNumber: TextView,
        apiService: TrackingApiService
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getTrackingInfo(orderId)
                Log.d("API_TEST", "¡Datos recibidos! ID: $orderId, Estado: ${response.globalStatus}")

                tvEtaDate.text = response.etaDate
                tvGlobalStatus.text = response.globalStatus
                tvContainerNumber.text = "Contenedor: ${response.containerNumber}"

                if (response.history != null) {
                    timelineAdapter.updateData(response.history)
                } else {

                    timelineAdapter.updateData(emptyList<TrackingStatus>())
                }
            } catch (e: Exception) {
                Log.e("API_TEST", "Error en la solicitud: ${e.message}")
            }
        }
    }
}
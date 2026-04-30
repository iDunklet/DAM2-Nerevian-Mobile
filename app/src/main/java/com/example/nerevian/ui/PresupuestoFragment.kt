package com.example.nerevian.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.example.nerevian.core.model.business.budget.Budget
import com.example.nerevian.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PresupuestoFragment : Fragment(R.layout.fragment_presupuesto) {

    private lateinit var adapter: PresupuestoAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PresupuestoAdapter(emptyList())
        recyclerView.adapter = adapter

        cargarDatosDesdeApi()
    }

    private fun cargarDatosDesdeApi() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getPresupuestos()

                if (response.isSuccessful && response.body() != null) {
                    val presupuestosReales = response.body()!!

                    withContext(Dispatchers.Main) {
                        adapter = PresupuestoAdapter(presupuestosReales)
                        recyclerView.adapter = adapter
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error de servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error de red. ¿Está el backend encendido?", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
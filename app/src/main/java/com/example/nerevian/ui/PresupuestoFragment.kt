package com.example.nerevian.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.nerevian.R
import com.example.nerevian.core.model.business.budget.Budget
import com.example.nerevian.core.model.business.budget.BudgetStatusUpdateRequest
import com.example.nerevian.data.network.RetrofitInstance
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PresupuestoFragment : Fragment(R.layout.fragment_presupuesto) {

    private lateinit var adapter: PresupuestoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView

    private var presupuestos: List<Budget> = emptyList()
    private var filtroActual: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        emptyView = view.findViewById(R.id.tvEmptyPresupuestos)

        adapter = PresupuestoAdapter(emptyList()) { presupuesto, nuevoEstado, motivo, onSuccess ->
            actualizarEstado(presupuesto, nuevoEstado, motivo, onSuccess)
        }
        recyclerView.adapter = adapter

        configurarFiltros(view)
        cargarDatosDesdeApi()
    }

    private fun configurarFiltros(view: View) {
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupPresupuesto)

        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            filtroActual = when (checkedIds.firstOrNull()) {
                R.id.chipPendiente -> "Pendiente"
                R.id.chipAceptado -> "Aceptado"
                R.id.chipRechazado -> "Rechazado"
                else -> null
            }
            aplicarFiltro()
        }
    }

    private fun cargarDatosDesdeApi() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getPresupuestos()

                if (response.isSuccessful && response.body() != null) {
                    presupuestos = response.body()!!

                    withContext(Dispatchers.Main) {
                        aplicarFiltro()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error de servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error de red. Esta el backend encendido?", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun actualizarEstado(
        presupuesto: Budget,
        nuevoEstado: String,
        motivo: String?,
        onSuccess: () -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.updatePresupuestoEstado(
                    presupuesto.id,
                    BudgetStatusUpdateRequest(nuevoEstado, motivo)
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        presupuesto.estado = nuevoEstado
                        aplicarFiltro()
                        onSuccess()
                        Toast.makeText(requireContext(), "Presupuesto actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "No se pudo actualizar el presupuesto", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun aplicarFiltro() {
        val filtrados = filtroActual?.let { estado ->
            presupuestos.filter { it.estado == estado }
        } ?: presupuestos

        adapter.submitList(filtrados)
        emptyView.visibility = if (filtrados.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (filtrados.isEmpty()) View.GONE else View.VISIBLE
    }
}

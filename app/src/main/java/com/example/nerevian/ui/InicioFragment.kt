package com.example.nerevian.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.nerevian.R
import com.example.nerevian.core.model.business.budget.Budget
import com.example.nerevian.data.network.RetrofitInstance
import com.example.nerevian.ui.game.GameActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    private lateinit var tvClienteNombre: TextView
    private lateinit var tvInicioResumen: TextView
    private lateinit var tvPendientesCount: TextView
    private lateinit var tvAceptadosCount: TextView
    private lateinit var tvRechazadosCount: TextView
    private lateinit var tvUltimoEstado: TextView
    private lateinit var tvUltimoId: TextView
    private lateinit var tvUltimoOrigen: TextView
    private lateinit var tvUltimoDestino: TextView
    private lateinit var tvUltimoExpira: TextView
    private lateinit var tvUltimoPrecio: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        configurarAcciones(view)
        mostrarUsuario()
        cargarResumen()
    }

    private fun bindViews(view: View) {
        tvClienteNombre = view.findViewById(R.id.tvClienteNombre)
        tvInicioResumen = view.findViewById(R.id.tvInicioResumen)
        tvPendientesCount = view.findViewById(R.id.tvPendientesCount)
        tvAceptadosCount = view.findViewById(R.id.tvAceptadosCount)
        tvRechazadosCount = view.findViewById(R.id.tvRechazadosCount)
        tvUltimoEstado = view.findViewById(R.id.tvUltimoEstado)
        tvUltimoId = view.findViewById(R.id.tvUltimoId)
        tvUltimoOrigen = view.findViewById(R.id.tvUltimoOrigen)
        tvUltimoDestino = view.findViewById(R.id.tvUltimoDestino)
        tvUltimoExpira = view.findViewById(R.id.tvUltimoExpira)
        tvUltimoPrecio = view.findViewById(R.id.tvUltimoPrecio)
    }

    private fun configurarAcciones(view: View) {
        view.findViewById<View>(R.id.btnVerPresupuestos).setOnClickListener {
            navegarA(R.id.nav_presupuestos)
        }

        view.findViewById<View>(R.id.tvVerPresupuestos).setOnClickListener {
            navegarA(R.id.nav_presupuestos)
        }

        view.findViewById<View>(R.id.cardPresupuestosPendientes).setOnClickListener {
            navegarA(R.id.nav_presupuestos)
        }

        view.findViewById<View>(R.id.cardUltimoPresupuesto).setOnClickListener {
            navegarA(R.id.nav_presupuestos)
        }

        view.findViewById<View>(R.id.btnVerTracking).setOnClickListener {
            navegarA(R.id.nav_tracking)
        }

        view.findViewById<View>(R.id.btnVerDocs).setOnClickListener {
            navegarA(R.id.nav_docs)
        }

        view.findViewById<View>(R.id.mobile).setOnClickListener {
            startActivity(Intent(requireContext(), GameActivity::class.java))
        }
    }

    private fun mostrarUsuario() {
        val userName = requireActivity().intent.getStringExtra("user_name")
            ?.takeIf { it.isNotBlank() }
            ?: "Cliente"

        tvClienteNombre.text = userName
    }

    private fun cargarResumen() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getPresupuestos()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        mostrarPresupuestos(response.body()!!)
                    } else {
                        mostrarResumenVacio()
                        Toast.makeText(requireContext(), "No se pudo cargar el inicio", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    mostrarResumenVacio()
                    Toast.makeText(requireContext(), "Inicio sin conexion con API", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarPresupuestos(presupuestos: List<Budget>) {
        val pendientes = presupuestos.count { it.estado == "Pendiente" }
        val aceptados = presupuestos.count { it.estado == "Aceptado" }
        val rechazados = presupuestos.count { it.estado == "Rechazado" }

        tvPendientesCount.text = pendientes.toString()
        tvAceptadosCount.text = aceptados.toString()
        tvRechazadosCount.text = rechazados.toString()
        tvInicioResumen.text = "${presupuestos.size} presupuestos disponibles"

        val destacado = presupuestos.firstOrNull { it.estado == "Pendiente" }
            ?: presupuestos.firstOrNull()

        if (destacado != null) {
            mostrarPresupuestoDestacado(destacado)
        } else {
            mostrarResumenVacio()
        }
    }

    private fun mostrarPresupuestoDestacado(presupuesto: Budget) {
        tvUltimoEstado.text = presupuesto.estado
        tvUltimoId.text = presupuesto.id
        tvUltimoOrigen.text = presupuesto.origen
        tvUltimoDestino.text = presupuesto.destino
        tvUltimoExpira.text = "Expira: ${presupuesto.expira}"
        tvUltimoPrecio.text = presupuesto.precio
    }

    private fun mostrarResumenVacio() {
        tvPendientesCount.text = "0"
        tvAceptadosCount.text = "0"
        tvRechazadosCount.text = "0"
        tvInicioResumen.text = "Sin presupuestos disponibles"
        tvUltimoEstado.text = "Sin datos"
        tvUltimoId.text = "--"
        tvUltimoOrigen.text = "--"
        tvUltimoDestino.text = "--"
        tvUltimoExpira.text = "Expira: --"
        tvUltimoPrecio.text = "--"
    }

    private fun navegarA(itemId: Int) {
        requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottomNavigation)
            .selectedItemId = itemId
    }

    companion object {
        @JvmStatic
        fun newInstance() = InicioFragment()
    }
}

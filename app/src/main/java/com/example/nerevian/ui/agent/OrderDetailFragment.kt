package com.example.nerevian.ui.agent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.nerevian.R
import com.example.nerevian.core.model.operations.OrderDetailResponse
import com.example.nerevian.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragmento que muestra el detalle completo de un pedido (orden).
 * Modularización: idealmente mover a módulo 'order' y usar ViewModel + Repository
 * para desacoplar la lógica de red de la UI.
 *
 * @see OrderDetailResponse
 */
class OrderDetailFragment : Fragment() {

    private var orderId: Int = -1

    // Vistas principales
    private lateinit var tvReferencia: TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvCliente: TextView
    private lateinit var tvIncoterm: TextView

    // Iconos de documentación (BL, Factura, Packing, DUA)
    private lateinit var ivCheckBL: ImageView
    private lateinit var ivCheckFactura: ImageView
    private lateinit var ivCheckPacking: ImageView
    private lateinit var ivCheckDua: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            orderId = it.getInt(ARG_ORDER_ID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_detail, container, false)
        initViews(view)
        return view
    }

    /** Inicializa todas las referencias a vistas y configura el botón back */
    private fun initViews(view: View) {
        tvReferencia = view.findViewById(R.id.tvReferencia)
        tvEstado = view.findViewById(R.id.tvEstadoDetalle)
        tvCliente = view.findViewById(R.id.tvClienteDetalle)
        tvIncoterm = view.findViewById(R.id.tvIncotermValue)

        ivCheckBL = view.findViewById(R.id.ivCheckBL)
        ivCheckFactura = view.findViewById(R.id.ivCheckFactura)
        ivCheckPacking = view.findViewById(R.id.ivCheckPacking)
        ivCheckDua = view.findViewById(R.id.ivCheckDua)

        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (orderId != -1) {
            fetchOrderDetails(orderId)
        }
    }

    /** Obtiene los detalles desde la API usando corutinas (IO + Main) */
    private fun fetchOrderDetails(id: Int) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("NEREVIAN_CHECK", "Solicitando detalles para ID: $id")
                val response = RetrofitInstance.api.getOrderDetails(id)

                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        updateUI(response.body()!!)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.e("NEREVIAN_CHECK", "Error API: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("NEREVIAN_CHECK", "Fallo de red: ${e.message}")
                }
            }
        }
    }

    /** Actualiza la UI con los datos recibidos y los iconos de documentos */
    private fun updateUI(detail: OrderDetailResponse) {
        tvReferencia.text = detail.referencia
        tvEstado.text = detail.estadoNombre
        tvCliente.text = detail.clienteNombre
        tvIncoterm.text = detail.incoterm

        setDocumentStatus(ivCheckBL, detail.tieneBL)
        setDocumentStatus(ivCheckFactura, detail.tieneFactura)
        setDocumentStatus(ivCheckPacking, detail.tienePacking)
        setDocumentStatus(ivCheckDua, detail.tieneDua)

        Log.d("NEREVIAN_CHECK", "UI Actualizada para: ${detail.referencia}")
    }

    /** Cambia el icono y color según disponibilidad del documento (check verde / X roja) */
    private fun setDocumentStatus(imageView: ImageView, isAvailable: Boolean) {
        if (isAvailable) {
            imageView.setImageResource(android.R.drawable.checkbox_on_background)
            imageView.setColorFilter(resources.getColor(android.R.color.holo_green_dark, null))
            imageView.alpha = 1.0f
        } else {
            imageView.setImageResource(android.R.drawable.ic_delete)
            imageView.setColorFilter(resources.getColor(android.R.color.holo_red_dark, null))
            imageView.alpha = 0.5f
        }
    }

    companion object {
        private const val ARG_ORDER_ID = "order_id"

        /** Factory method para crear una instancia con el ID del pedido */
        @JvmStatic
        fun newInstance(orderId: Int) = OrderDetailFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_ORDER_ID, orderId)
            }
        }
    }
}
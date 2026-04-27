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

class OrderDetailFragment : Fragment() {

    private var orderId: Int = -1

    // Vistas del Layout
    private lateinit var tvReferencia: TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvCliente: TextView
    private lateinit var tvIncoterm: TextView

    // Iconos de Documentación
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

    private fun initViews(view: View) {
        // Vinculación con los IDs exactos de tu XML
        tvReferencia = view.findViewById(R.id.tvReferencia)
        tvEstado = view.findViewById(R.id.tvEstadoDetalle)
        tvCliente = view.findViewById(R.id.tvClienteDetalle)
        tvIncoterm = view.findViewById(R.id.tvIncotermValue)

        ivCheckBL = view.findViewById(R.id.ivCheckBL)
        ivCheckFactura = view.findViewById(R.id.ivCheckFactura)
        ivCheckPacking = view.findViewById(R.id.ivCheckPacking)
        ivCheckDua = view.findViewById(R.id.ivCheckDua)

        // Botón volver atrás
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

    private fun updateUI(detail: OrderDetailResponse) {
        tvReferencia.text = detail.referencia
        tvEstado.text = detail.estadoNombre
        tvCliente.text = detail.clienteNombre
        tvIncoterm.text = detail.incoterm

        // Usamos iconos nativos de Android y colores del sistema para evitar errores de recursos
        setDocumentStatus(ivCheckBL, detail.tieneBL)
        setDocumentStatus(ivCheckFactura, detail.tieneFactura)
        setDocumentStatus(ivCheckPacking, detail.tienePacking)
        setDocumentStatus(ivCheckDua, detail.tieneDua)

        Log.d("NEREVIAN_CHECK", "UI Actualizada para: ${detail.referencia}")
    }

    private fun setDocumentStatus(imageView: ImageView, isAvailable: Boolean) {
        if (isAvailable) {
            // Icono de "Check" nativo en verde
            imageView.setImageResource(android.R.drawable.checkbox_on_background)
            imageView.setColorFilter(resources.getColor(android.R.color.holo_green_dark, null))
            imageView.alpha = 1.0f
        } else {
            // Icono de "X" nativo en rojo
            imageView.setImageResource(android.R.drawable.ic_delete)
            imageView.setColorFilter(resources.getColor(android.R.color.holo_red_dark, null))
            imageView.alpha = 0.5f
        }
    }

    companion object {
        private const val ARG_ORDER_ID = "order_id"
        @JvmStatic
        fun newInstance(orderId: Int) =
            OrderDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ORDER_ID, orderId)
                }
            }
    }
}
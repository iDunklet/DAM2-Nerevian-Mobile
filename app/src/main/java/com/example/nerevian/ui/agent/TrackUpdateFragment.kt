package com.example.nerevian.ui.agent

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nerevian.R
import com.example.nerevian.data.network.RetrofitInstance
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackUpdateFragment : Fragment() {

    private var orderId: Int = -1
    private var selectedStatusId: Int = -1

    // Referencias a las vistas
    private lateinit var tvReferencia: TextView
    private lateinit var tvEmpresa: TextView
    private lateinit var btnConfirmar: MaterialButton
    private lateinit var statusCards: List<MaterialCardView>

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
        // Inflamos la vista de forma tradicional
        val view = inflater.inflate(R.layout.fragment_track_update, container, false)

        initViews(view)

        return view
    }

    private fun initViews(view: View) {
        // 1. Vincular textos y botones básicos
        tvReferencia = view.findViewById(R.id.tvReferencia)
        tvEmpresa = view.findViewById(R.id.tvEmpresa)
        btnConfirmar = view.findViewById(R.id.btnConfirmar)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)

        // 2. Vincular las 7 tarjetas del Grid
        statusCards = listOf(
            view.findViewById(R.id.cardStatus1),
            view.findViewById(R.id.cardStatus2),
            view.findViewById(R.id.cardStatus3),
            view.findViewById(R.id.cardStatus4),
            view.findViewById(R.id.cardStatus5),
            view.findViewById(R.id.cardStatus6),
            view.findViewById(R.id.cardStatus7)
        )

        // 3. Configurar clics de las tarjetas
        setupSelectionLogic()

        // 4. Configurar botón confirmar
        btnConfirmar.setOnClickListener {
            if (selectedStatusId == -1) {
                Toast.makeText(requireContext(), "Selecciona un estado primero", Toast.LENGTH_SHORT).show()
            } else {
                enviarActualizacion()
            }
        }

        // 5. Configurar botón atrás
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupSelectionLogic() {
        statusCards.forEachIndexed { index, card ->
            card.setOnClickListener {
                // El ID del estado será el índice + 1 (1 al 7)
                selectedStatusId = index + 1
                resaltarTarjetaSeleccionada(card)
            }
        }
    }

    private fun resaltarTarjetaSeleccionada(selectedCard: MaterialCardView) {
        // Resetear todas las tarjetas al estado original (Blanco)
        statusCards.forEach { card ->
            card.setCardBackgroundColor(Color.WHITE)
            card.strokeColor = Color.parseColor("#E0E0E0")
        }

        // Aplicar estilo de selección a la elegida (Verde Nerevian)
        selectedCard.setCardBackgroundColor(Color.parseColor("#E6F2ED"))
        selectedCard.strokeColor = Color.parseColor("#0C3B3B")
    }

    private fun enviarActualizacion() {
        btnConfirmar.isEnabled = false
        btnConfirmar.text = "Actualizando..."

        // Llamada a la API usando tu RetrofitInstance
        RetrofitInstance.api.changeStatus(orderId, selectedStatusId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Estado actualizado correctamente", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Error en el servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                        resetBoton()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                    resetBoton()
                }
            })
    }

    private fun resetBoton() {
        btnConfirmar.isEnabled = true
        btnConfirmar.text = "Confirmar Actualización"
    }

    companion object {
        private const val ARG_ORDER_ID = "order_id"

        @JvmStatic
        fun newInstance(orderId: Int) =
            TrackUpdateFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ORDER_ID, orderId)
                }
            }
    }
}
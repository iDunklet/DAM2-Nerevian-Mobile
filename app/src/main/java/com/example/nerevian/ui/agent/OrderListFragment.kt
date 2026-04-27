package com.example.nerevian.ui.agent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.example.nerevian.data.network.RetrofitInstance
import com.example.nerevian.ui.agent.TrackUpdateFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderListFragment : Fragment() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Inflamos la vista
        val view = inflater.inflate(R.layout.fragment_order_list, container, false)

        // Buscamos el RecyclerView
        rvOrders = view.findViewById(R.id.rvOrders)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2. Configuramos la lista
        setupRecyclerView()

        // 3. Pedimos los datos al servidor
        fetchOrders()
    }

    private fun setupRecyclerView() {
        // Le decimos que se comporte como una lista vertical
        rvOrders.layoutManager = LinearLayoutManager(requireContext())

        // Inicializamos el adaptador vacío de momento y configuramos los clicks
        adapter = OrderAdapter(
            orders = emptyList(),
            onDetailsClick = { orderId ->
                val detailFragment = OrderDetailFragment.newInstance(orderId)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailFragment) // Cambia fragment_container por el ID real de tu MainActivity
                    .addToBackStack(null) // Para que el botón "Atrás" funcione
                    .commit()
            },
            onUpdateClick = { orderId ->
                val updateFragment = TrackUpdateFragment.Companion.newInstance(orderId)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, updateFragment)
                    .addToBackStack(null)
                    .commit()
            }
        )

        // ¡ATENCIÓN! Esta es la línea que arregla tu error de "No adapter attached"
        rvOrders.adapter = adapter
    }

    private fun fetchOrders() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            Log.d("NEREVIAN_CHECK", "--- INICIANDO PETICIÓN API ---")
            try {
                val response = RetrofitInstance.api.getAllTracks()

                if (response.isSuccessful) {
                    val orderList = response.body()

                    // LOG 1: Verificar si el cuerpo viene nulo
                    if (orderList == null) {
                        Log.w("NEREVIAN_CHECK", "Respuesta exitosa pero el BODY está VACÍO (null)")
                        return@launch
                    }

                    // LOG 2: Verificar cantidad de elementos y contenido del primero
                    Log.d("NEREVIAN_CHECK", "¡Datos recibidos con éxito!")
                    Log.d("NEREVIAN_CHECK", "Cantidad de pedidos: ${orderList.size}")

                    if (orderList.isNotEmpty()) {
                        val firstOrder = orderList[0]
                        // Aquí imprimimos el primer objeto para ver si los campos coinciden
                        Log.d("NEREVIAN_CHECK", "Primer pedido recibido: ID=${firstOrder.id}, Ref=${firstOrder.referenceCode}")
                    } else {
                        Log.w("NEREVIAN_CHECK", "La lista de pedidos llegó vacía (0 elementos)")
                    }

                    withContext(Dispatchers.Main) {
                        adapter.updateData(orderList)
                        Log.d("NEREVIAN_CHECK", "Adaptador actualizado en el Hilo Principal")
                    }
                } else {
                    // LOG 3: Error de código (404, 500, etc.)
                    val errorRaw = response.errorBody()?.string()
                    Log.e("NEREVIAN_CHECK", "Error del servidor: ${response.code()}")
                    Log.e("NEREVIAN_CHECK", "Cuerpo del error: $errorRaw")
                }

            } catch (e: Exception) {
                // LOG 4: Errores de conexión o parseo (GSON Error)
                Log.e("NEREVIAN_CHECK", "ERROR CRÍTICO: ${e.message}")
                e.printStackTrace()
            }
            Log.d("NEREVIAN_CHECK", "--- FIN DE LA PETICIÓN ---")
        }
    }
}
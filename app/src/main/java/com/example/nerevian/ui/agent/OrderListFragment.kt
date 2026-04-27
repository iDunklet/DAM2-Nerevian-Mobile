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

/**
 * Fragmento que muestra la lista de pedidos (órdenes) obtenidos desde la API.
 * Modularización: mover a módulo 'order' y usar ViewModel + Repository para separar
 * la obtención de datos de la UI. Considerar DiffUtil en el adaptador.
 *
 * @see OrderAdapter
 * @see OrderDetailFragment
 */
class OrderListFragment : Fragment() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_list, container, false)
        rvOrders = view.findViewById(R.id.rvOrders)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchOrders()
    }

    /** Configura el RecyclerView con LayoutManager y adaptador, definiendo los callbacks */
    private fun setupRecyclerView() {
        rvOrders.layoutManager = LinearLayoutManager(requireContext())

        adapter = OrderAdapter(
            orders = emptyList(),
            onDetailsClick = { orderId ->
                val detailFragment = OrderDetailFragment.newInstance(orderId)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onUpdateClick = { orderId ->
                val updateFragment = TrackUpdateFragment.newInstance(orderId)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, updateFragment)
                    .addToBackStack(null)
                    .commit()
            }
        )

        rvOrders.adapter = adapter
    }

    /** Obtiene la lista de pedidos desde la API usando corutinas (IO + Main) */
    private fun fetchOrders() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            Log.d("NEREVIAN_CHECK", "--- INICIANDO PETICIÓN API ---")
            try {
                val response = RetrofitInstance.api.getAllTracks()

                if (response.isSuccessful) {
                    val orderList = response.body()

                    if (orderList == null) {
                        Log.w("NEREVIAN_CHECK", "Respuesta exitosa pero el BODY está VACÍO (null)")
                        return@launch
                    }

                    Log.d("NEREVIAN_CHECK", "¡Datos recibidos con éxito!")
                    Log.d("NEREVIAN_CHECK", "Cantidad de pedidos: ${orderList.size}")

                    if (orderList.isNotEmpty()) {
                        val firstOrder = orderList[0]
                        Log.d("NEREVIAN_CHECK", "Primer pedido recibido: ID=${firstOrder.id}, Ref=${firstOrder.referenceCode}")
                    } else {
                        Log.w("NEREVIAN_CHECK", "La lista de pedidos llegó vacía (0 elementos)")
                    }

                    withContext(Dispatchers.Main) {
                        adapter.updateData(orderList)
                        Log.d("NEREVIAN_CHECK", "Adaptador actualizado en el Hilo Principal")
                    }
                } else {
                    val errorRaw = response.errorBody()?.string()
                    Log.e("NEREVIAN_CHECK", "Error del servidor: ${response.code()}")
                    Log.e("NEREVIAN_CHECK", "Cuerpo del error: $errorRaw")
                }

            } catch (e: Exception) {
                Log.e("NEREVIAN_CHECK", "ERROR CRÍTICO: ${e.message}")
                e.printStackTrace()
            }
            Log.d("NEREVIAN_CHECK", "--- FIN DE LA PETICIÓN ---")
        }
    }
}
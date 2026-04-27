package com.example.nerevian.core.model.operations

import com.google.gson.annotations.SerializedName

data class OrderDetailResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("operacio_ref") val referencia: String,
    @SerializedName("estat_actual") val estadoNombre: String,
    @SerializedName("puerto_origen") val clienteNombre: String,
    @SerializedName("tipo_carga") val incoterm: String,
    @SerializedName("puerto_destino") val destinoNombre: String,
    @SerializedName("bl") val tieneBL: Boolean = false,
    @SerializedName("factura") val tieneFactura: Boolean = false,
    @SerializedName("packing_list") val tienePacking: Boolean = false,
    @SerializedName("dua") val tieneDua: Boolean = false,

    @SerializedName("peso") val peso: String? = null,
    @SerializedName("volumen") val volumen: String? = null,
    @SerializedName("num_contenedor") val numContenedor: String? = null,

    // Aquí es donde se usa la clase de abajo
    @SerializedName("historial") val historial: List<TimelineEvent> = emptyList()
)

// ESTA ES LA CLASE QUE TE FALTA DEFINIR:
data class TimelineEvent(
    @SerializedName("fecha") val fecha: String,
    @SerializedName("hora") val hora: String?,
    @SerializedName("evento") val evento: String
)
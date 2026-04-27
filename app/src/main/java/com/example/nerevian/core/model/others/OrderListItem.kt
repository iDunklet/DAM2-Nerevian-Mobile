package com.example.nerevian.core.model.operations

import com.google.gson.annotations.SerializedName

data class OrderListItem(
    @SerializedName("id") val id: Int,
    @SerializedName("reference") val referenceCode: String,
    @SerializedName("status") val status: String,
    @SerializedName("ruta") val ruta: String?,

    // Añadimos estos para que el OrderAdapter del Agente no explote:
    @SerializedName("cliente_nombre") val clientName: String = "Cliente",
    @SerializedName("puerto_origen") val originPort: String = "N/A",
    @SerializedName("puerto_destino") val destinationPort: String = "N/A",
    @SerializedName("data_inici") val eta: String? = null
)
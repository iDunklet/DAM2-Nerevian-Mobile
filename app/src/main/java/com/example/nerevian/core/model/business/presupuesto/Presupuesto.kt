package com.example.nerevian.core.model.business.presupuesto

data class Presupuesto(
    val id: String,
    val origen: String,
    val destino: String,
    val tipo: String,
    val expira: String,
    val precio: String,
    val incoterm: String,
    val detalle: String,
    var isExpanded: Boolean = false
)

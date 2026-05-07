package com.example.nerevian.core.model.business.budget

data class Budget(
    val id: String,
    val origen: String,
    val destino: String,
    val tipo: String,
    val expira: String,
    val precio: String,
    val incoterm: String,
    var estado: String,
    var isExpanded: Boolean = false
)

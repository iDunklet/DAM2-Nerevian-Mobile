package com.example.nerevian.core.model.business.budget

data class CreateBudgetRequest(
    val solicitudId: Int,
    val clientId: Int,
    val presupuesto: Double,
    val moneda: String = "EUR",
    val fechaValidezFinal: String,
    val comentarios: String? = null,
    val esContraoferta: Boolean = false
)

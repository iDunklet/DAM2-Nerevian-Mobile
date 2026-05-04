package com.example.nerevian.core.model.business.budget

data class BudgetStatusUpdateRequest(
    val estado: String,
    val motivo: String? = null
)

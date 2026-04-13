package com.example.nerevian.core.model.incoterms


data class TrackingOrder(
    val referenceCode: String,
    val route: String,

    val etaDate: String,
    val globalStatus: String,
    val containerNumber: String
)

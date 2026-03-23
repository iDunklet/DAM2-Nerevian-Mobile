package com.example.nerevian.core.model.business.offer

import com.example.nerevian.core.model.business.request.Request
import com.example.nerevian.core.model.user.Client
import java.sql.Date
import java.time.LocalDate


data class Offer (
    val id: Int,
    var creationDate: LocalDate,
    var initialValidityDate: LocalDate,
    var finalValidationDate: LocalDate,
    var moneda: String,
    var clients: Array<Client>,
    var budget: Double,
    var coments: ArrayList<String>,
    var statusOffer: StatusOffer,
    var request: Request,
    var denyReason: String
    )
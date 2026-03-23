package com.example.nerevian.core.model.operations

import com.example.nerevian.core.model.business.offer.Offer
import com.example.nerevian.core.model.user.Client
import com.example.nerevian.core.model.user.User
import java.time.LocalDate

data class Operation (
    val id: Int,
    val offer: Offer,
    var referenceCode: String,
    var status: OperationStatus,
    val operator: User,
    val client: Client,
    var originDate: LocalDate,
    var finalDate: LocalDate,
    var observations: Array<String>,

    )
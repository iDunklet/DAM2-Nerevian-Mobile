package com.example.nerevian.core.model.business.request

import com.example.nerevian.core.model.incoterms.IncotermType
import com.example.nerevian.core.model.logistics.CargoType
import com.example.nerevian.core.model.logistics.Carrier
import com.example.nerevian.core.model.logistics.ContainerType
import com.example.nerevian.core.model.logistics.FlowType
import com.example.nerevian.core.model.logistics.Port
import com.example.nerevian.core.model.logistics.TransportType
import com.example.nerevian.core.model.user.Client
import com.example.nerevian.core.model.user.User
import com.example.nerevian.core.model.others.ValidationType
import java.time.LocalDate

data class Request (
    val id: Int,
    val transportType: TransportType,
    val flowType: FlowType,
    val cargoType: CargoType,
    val incotermType: IncotermType,
    val client: Client,
    var comments: String?,
    val carrier: Carrier?,
    var rawWeight: Float,
    var volume: Float,
    val validationType: ValidationType,
    val originPort: Port?,
    val destinationPort: Port?,
    val status: StatusRequest,
    val operator: User,
    val creationDate: LocalDate,
    val containerType: ContainerType?



    )
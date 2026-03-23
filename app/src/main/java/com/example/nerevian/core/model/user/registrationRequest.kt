package com.example.nerevian.core.model.user

import java.sql.Date
import java.time.LocalDate

data class registrationRequest (
    val id: Int,
    var companyName: String,
    var contact: String,
    var phoneNumber: String?,
    var message: String?,
    var status: String?,
    var reason: String?,
    var creationDate: LocalDate,
    var resolutionDate: LocalDate,
    var resolvedBy: User
)
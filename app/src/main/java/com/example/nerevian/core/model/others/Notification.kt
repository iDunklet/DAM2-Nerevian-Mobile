package com.example.nerevian.core.model.others

import com.example.nerevian.core.model.business.request.Request
import com.example.nerevian.core.model.incoterms.IncotermType
import java.time.LocalDate

data class Notification (
    val id: Int,
    val incotermType: IncotermType,
    val request: Request,
    var updateDate: LocalDate
)
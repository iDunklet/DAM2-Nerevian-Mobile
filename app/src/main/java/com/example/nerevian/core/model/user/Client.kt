package com.example.nerevian.core.model.user

import java.time.LocalDate
import java.util.Date

data class Client (
    val id: Int,
    val user: User,
    val dniId: DNI,
    val registerDate: LocalDate

    )
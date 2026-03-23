package com.example.nerevian.core.model.user

data class User (
    val id: Int,
    var name: String,
    var email: String,
    var phoneNumber: String,
    var password: String,
    var role: Role
)
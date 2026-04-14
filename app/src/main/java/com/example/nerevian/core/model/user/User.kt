package com.example.nerevian.core.model.user

import com.google.gson.annotations.SerializedName

data class User (
    val id: Int,
    @SerializedName("nom") var name: String, // Ajusta "nom" si en la BD se llama diferente
    @SerializedName("correu") var email: String,
    @SerializedName("telefon") var phoneNumber: String, // Ajusta el nombre de la BD
    @SerializedName("contrasenya") var password: String,
    var role: Role
)
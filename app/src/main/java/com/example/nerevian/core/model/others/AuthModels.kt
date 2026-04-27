package com.example.nerevian.core.model.others

import com.example.nerevian.core.model.user.User
import com.google.gson.annotations.SerializedName

// Lo que enviamos al servidor
data class LoginRequest(
    @SerializedName("correu") val correu: String,
    @SerializedName("contrasenya") val contrasenya: String
)

// Lo que responde el servidor (Aquí usamos TU modelo User)
data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: User
)
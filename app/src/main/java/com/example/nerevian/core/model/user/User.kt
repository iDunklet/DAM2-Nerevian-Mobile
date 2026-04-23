// app/src/main/java/com/example/nerevian/core/model/user/User.kt
package com.example.nerevian.core.model.user

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,

    @SerializedName("firstName") // Coincide con el Select de C#
    val name: String,

    @SerializedName("lastName")  // Coincide con el Select de C#
    val surname: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")     // Coincide con el Select de C#
    val phoneNumber: String?,

    @SerializedName("roleId")
    val roleId: Int
)
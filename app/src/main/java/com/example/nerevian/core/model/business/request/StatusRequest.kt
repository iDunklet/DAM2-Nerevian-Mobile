package com.example.nerevian.core.model.business.request

import com.google.gson.annotations.SerializedName

data class StatusRequest (
    @SerializedName("id")
    val id: Int,

    @SerializedName("status")
    val status: String
)
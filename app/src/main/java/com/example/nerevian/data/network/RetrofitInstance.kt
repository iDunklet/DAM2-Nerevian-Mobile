package com.example.nerevian.data.network

import retrofit2.converter.gson.GsonConverterFactory // <-- SI ESTA FALTA, GSON PETARÁ
import com.example.nerevian.data.network.ApiService
import kotlin.getValue
import retrofit2.Retrofit
object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:5125/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
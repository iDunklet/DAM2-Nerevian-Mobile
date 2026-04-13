package com.example.nerevian.data.network

import retrofit2.converter.gson.GsonConverterFactory // <-- SI ESTA FALTA, GSON PETARÁ
import com.example.nerevian.data.network.ApiService
import kotlin.getValue
import retrofit2.Retrofit
object RetrofitInstance {
    private const val BASE_URL = "http://simex03-nereviannetapi-ectkq2-71fb90-51-83-192-177.traefik.me/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
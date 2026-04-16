package com.example.nerevian.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL_MIA = "http://simex03-nereviannetapi-ectkq2-71fb90-51-83-192-177.traefik.me/"

    private const val BASE_URL_AUTH = "https://nerevian.xyz/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_MIA)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val authApi: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_AUTH)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
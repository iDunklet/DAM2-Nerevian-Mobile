package com.example.nerevian.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL_MAIN = "http://10.0.2.2:5125"
    private const val BASE_URL_AUTH = "https://nerevian.xyz/"

    val api: ApiService by lazy { createRetrofit(BASE_URL_MAIN) }
    val authApi: ApiService by lazy { createRetrofit(BASE_URL_AUTH) }

    private fun createRetrofit(baseUrl: String): ApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
package com.example.nerevian.data.network

import com.example.nerevian.core.model.incoterms.TrackingStatus
import retrofit2.http.GET
import retrofit2.http.Path

data class TrackingResponse(
    val referenceCode: String,
    val route: String,
    val etaDate: String,
    val globalStatus: String,
    val containerNumber: String,

    val history: List<TrackingStatus>? = null
)

interface TrackingApiService {
    @GET("api/TrackingETA/{id}")
    suspend fun getTrackingInfo(@Path("id") id: String): TrackingResponse
}
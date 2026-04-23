package com.example.nerevian.data.network
import com.example.nerevian.core.model.business.request.StatusRequest
import com.example.nerevian.core.model.user.User
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path



interface ApiService {
    @GET("api/Track/{id}")
    suspend fun getTrackStatus(@Path("id") id: Int): Response<StatusRequest>

    @PUT("api/Track/{id}/estado/{nuevoEstadoId}")
    suspend fun changeTrackStatus(
        @Path("id") id: Int,
        @Path("nuevoEstadoId") newStatusId: Int
    ): Response<Int>
    @POST("api/login") // Cambia "api/login" si la URL en Laravel es distinta
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/user/profile/{id}")
    suspend fun getUserProfile(@Path("id") userId: Int): Response<User>

}



package com.example.nerevian.data.network

import com.example.nerevian.core.model.business.request.StatusRequest
import com.example.nerevian.core.model.operations.OrderDetailResponse
import com.example.nerevian.core.model.operations.OrderListItem
import com.example.nerevian.core.model.others.LoginRequest
import com.example.nerevian.core.model.others.LoginResponse
import com.example.nerevian.core.model.user.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // --- ENDPOINTS DE OPERACIONES (Antes Track) ---

    // Obtiene el detalle completo (incluyendo pes_brut e historial para tu amigo)
    @GET("api/operation/{id}") // <--- Busca esto
    suspend fun getOrderDetails(@Path("id") id: Int): Response<OrderDetailResponse>

    // Obtiene la lista de tracks para el RecyclerView
    @GET("api/operation")
    suspend fun getAllTracks(): Response<List<OrderListItem>>

    // Actualiza el estado de la operación
    @PUT("api/operation/{id}/estado/{nuevoEstadoId}")
    suspend fun changeTrackStatus(
        @Path("id") id: Int,
        @Path("nuevoEstadoId") newStatusId: Int
    ): Response<Unit>

    // --- OTROS ENDPOINTS ---

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/user/profile/{id}")
    suspend fun getUserProfile(@Path("id") id: Int): Response<User>

    @GET("api/operation/{id}")
    suspend fun getTrackStatus(@Path("id") id: Int): Response<StatusRequest>

    @PUT("api/operation/{id}/estado/{nuevoEstadoId}")
    fun changeStatus(
        @Path("id") id: Int,
        @Path("nuevoEstadoId") statusId: Int
    ): Call<Void>
}
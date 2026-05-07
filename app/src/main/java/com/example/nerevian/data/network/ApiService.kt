package com.example.nerevian.data.network
import com.example.nerevian.core.model.business.budget.Budget
import com.example.nerevian.core.model.business.budget.CreateBudgetRequest
import com.example.nerevian.core.model.business.budget.BudgetStatusUpdateRequest
import com.example.nerevian.core.model.business.request.StatusRequest
import com.example.nerevian.core.model.documents.DocumentUploadRequest

import com.example.nerevian.core.model.documents.GenericResponse
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

    @POST("api/documents")
    suspend fun uploadDocumentInfo(
        @Body request: DocumentUploadRequest
    ): retrofit2.Response<GenericResponse>

    @GET("api/Presupuestos")
    suspend fun getPresupuestos(): Response<List<Budget>>

    @POST("api/Presupuestos")
    suspend fun createPresupuesto(
        @Body request: CreateBudgetRequest
    ): Response<GenericResponse>

    @PUT("api/Presupuestos/{id}/estado")
    suspend fun updatePresupuestoEstado(
        @Path("id") id: String,
        @Body request: BudgetStatusUpdateRequest
    ): Response<GenericResponse>

}




package com.example.nerevian.data.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ChatService {
    private val client = OkHttpClient()
    private val url = "https://n8n.nerevian.xyz/webhook/3e2f1022-a06a-40d0-a401-077128195595/chat"
    private val webhookUrl = "https://n8n.nerevian.xyz/webhook-test/3e2f1022-a06a-40d0-a401-077128195595/chat"
    suspend fun sendMessage(userText: String): String? = withContext(Dispatchers.IO) {
        try {
            // 1. Crear el cuerpo JSON para n8n
            val json = JSONObject()
            json.put("chatInput", userText)

            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

            // 2. Configurar la petición POST
            val request = Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build()

            // 3. Ejecutar y obtener respuesta
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext "Error del servidor: ${response.code}"

                // n8n suele devolver la respuesta directamente o en un JSON
                val responseBody = response.body?.string()

                // Si n8n devuelve un JSON como {"output": "Hola"}, lo extraemos:
                // val output = JSONObject(responseBody).getString("output")
                // return@withContext output

                return@withContext responseBody
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Error de conexión: ${e.message}"
        }
    }
}
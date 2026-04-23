package com.example.nerevian.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.net.Socket

object RetrofitInstance {

    private const val BASE_URL_MAIN = "http://simex03-nereviannetapi-ectkq2-71fb90-51-83-192-177.traefik.me/"
    private const val BASE_URL_AUTH = "https://nerevian.xyz/"

    // Configuración de Socket (Asegúrate de que la IP sea correcta para tu entorno)
    private const val SOCKET_HOST = "10.0.2.2"
    private const val SOCKET_PORT = 8090
    private const val TAG = "SocketInstance"

    val api: ApiService by lazy { createRetrofit(BASE_URL_MAIN) }
    val authApi: ApiService by lazy { createRetrofit(BASE_URL_AUTH) }

    private fun createRetrofit(baseUrl: String): ApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /**
     * Sube "ID|BASE64" al servidor de Sockets.
     */
    suspend fun uploadDataViaSocket(message: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            Log.d(TAG, "Conectando para SUBIDA...")
            Socket(SOCKET_HOST, SOCKET_PORT).use { socket ->
                socket.soTimeout = 15000
                val writer = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                // Enviamos el mensaje (ID|BASE64)
                writer.println(message)

                val response = reader.readLine()
                response == "OK"
            }
        }.getOrElse { e ->
            Log.e(TAG, "Error en upload: ${e.message}")
            false
        }
    }

    /**
     * Envía "DOWNLOAD|ID" y recibe el Base64 puro desde el servidor.
     */
    suspend fun downloadDataViaSocket(command: String): String = withContext(Dispatchers.IO) {
        runCatching {
            Log.d(TAG, "Conectando para DESCARGA con: $command")
            Socket(SOCKET_HOST, SOCKET_PORT).use { socket ->
                socket.soTimeout = 20000 // Mayor tiempo para recibir la imagen

                val writer = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                // 1. Enviamos el comando solicitado (DOWNLOAD|ID)
                writer.println(command)

                // 2. Leemos la respuesta (El servidor envía el Base64 en una sola línea larga)
                val response = reader.readLine() ?: ""

                if (response.startsWith("ERROR")) {
                    Log.e(TAG, "Respuesta del servidor: $response")
                    response // Devolvemos el error para manejarlo en la UI
                } else {
                    Log.d(TAG, "Datos recibidos. Tamaño: ${response.length}")
                    response
                }
            }
        }.getOrElse { e ->
            Log.e(TAG, "Error en download: ${e.message}")
            "ERROR: CONNECTION_FAILED"
        }
    }
}
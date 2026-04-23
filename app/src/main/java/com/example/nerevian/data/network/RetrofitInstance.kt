package com.example.nerevian.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

object RetrofitInstance {

    // API Configuration Constants
    private const val BASE_URL_MAIN = "http://simex03-nereviannetapi-ectkq2-71fb90-51-83-192-177.traefik.me/"
    private const val BASE_URL_AUTH = "https://nerevian.xyz/"

    // Socket Configuration Constants
    // 10.0.2.2 is the localhost alias for the Android Emulator to reach the host machine
    private const val SOCKET_HOST = "10.0.2.2"
    private const val SOCKET_PORT = 8090
    private const val TAG = "SocketInstance"

    // Lazy initialization for the main API client
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_MAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Lazy initialization for the authentication API client
    val authApi: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_AUTH)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Uploads the encrypted Base64 string to the local server via Sockets
    suspend fun uploadDataViaSocket(data: String): Boolean {
        return withContext(Dispatchers.IO) {
            var socket: Socket? = null
            var isSuccess = false

            try {
                Log.d(TAG, "Connecting to local host ($SOCKET_HOST:$SOCKET_PORT)...")
                socket = Socket(SOCKET_HOST, SOCKET_PORT)

                // Initialize output and input streams
                val outStream = PrintWriter(socket.getOutputStream(), true)
                val inStream = BufferedReader(InputStreamReader(socket.getInputStream()))

                // Send the encrypted data to the server (println adds the expected newline)
                outStream.println(data)

                // Wait for the server acknowledgement
                val response = inStream.readLine()
                if (response == "OK") {
                    Log.d(TAG, "Server confirmed file storage successfully!")
                    isSuccess = true
                } else {
                    Log.e(TAG, "Server returned an error.")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Socket connection error: ${e.message}")
            } finally {
                // Always close the socket to prevent memory leaks
                socket?.close()
            }

            isSuccess
        }
    }

    // Requests the last uploaded encrypted DNI from the local server
    suspend fun downloadDataViaSocket(): String {
        return withContext(Dispatchers.IO) {
            var socket: Socket? = null
            var downloadedData = ""

            try {
                Log.d(TAG, "Requesting file download from $SOCKET_HOST:$SOCKET_PORT...")
                socket = Socket(SOCKET_HOST, SOCKET_PORT)

                // Initialize output and input streams
                val outStream = PrintWriter(socket.getOutputStream(), true)
                val inStream = BufferedReader(InputStreamReader(socket.getInputStream()))

                // Send the download command trigger to the server
                outStream.println("DOWNLOAD")

                // Read the server response containing the encrypted Base64 string
                val response = inStream.readLine()

                if (!response.isNullOrEmpty() && response != "ERROR") {
                    Log.d(TAG, "Encrypted file successfully received from server.")
                    downloadedData = response
                } else {
                    Log.e(TAG, "Failed to download file. Server returned empty or error.")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Socket download error: ${e.message}")
            } finally {
                // Always close the socket to prevent memory leaks
                socket?.close()
            }

            downloadedData
        }
    }
}
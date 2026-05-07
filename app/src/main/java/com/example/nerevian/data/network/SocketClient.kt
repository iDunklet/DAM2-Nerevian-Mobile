package com.example.nerevian.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

/**
 * Cliente socket para enviar/recibir el DNI cifrado (binario) al servidor.
 */
object SocketClient {

    // ==================== CONSTANTES ====================
    private const val SOCKET_HOST = "10.0.2.2"   // IP del emulador Android (localhost del PC)
    private const val SOCKET_PORT = 8090
    private const val TIMEOUT_MS = 30000
    private const val TAG = "SocketClient"

    // ==================== SUBIDA ====================

    /**
     * Envía los bytes cifrados al servidor.
     * @return true si el servidor respondió "OK", false en otro caso.
     */
    suspend fun uploadDni(userId: Int, encryptedBytes: ByteArray): Boolean =
        withContext(Dispatchers.IO) {
            var exito = false  // única variable de retorno
            runCatching {
                Log.d(TAG, "Conectando para SUBIDA...")
                Socket(SOCKET_HOST, SOCKET_PORT).use { socket ->
                    socket.soTimeout = TIMEOUT_MS
                    // Flujos de datos para enviar/recibir tipos primitivos y UTF
                    val out = DataOutputStream(socket.getOutputStream())
                    val input = DataInputStream(socket.getInputStream())

                    // Protocolo: acción, userId, tamaño (long), bytes
                    out.writeUTF("UPLOAD")
                    out.writeInt(userId)
                    out.writeLong(encryptedBytes.size.toLong())
                    out.write(encryptedBytes)
                    out.flush()

                    val response = input.readUTF()
                    Log.d(TAG, "Respuesta: $response")
                    exito = response == "OK"
                }
            }.getOrElse { e ->
                Log.e(TAG, "Error en upload: ${e.message}")
                // exito ya es false
            }
            exito  // único punto de retorno
        }

    // ==================== DESCARGA ====================

    /**
     * Recupera los bytes cifrados del servidor.
     * @return ByteArray con los datos, o null si no existen o hay error.
     */
    suspend fun downloadDni(userId: Int): ByteArray? =
        withContext(Dispatchers.IO) {
            var resultado: ByteArray? = null  // única variable de retorno
            runCatching {
                Log.d(TAG, "Conectando para DESCARGA... ID: $userId")
                Socket(SOCKET_HOST, SOCKET_PORT).use { socket ->
                    socket.soTimeout = TIMEOUT_MS
                    val out = DataOutputStream(socket.getOutputStream())
                    val input = DataInputStream(socket.getInputStream())

                    out.writeUTF("DOWNLOAD")
                    out.writeInt(userId)
                    out.flush()

                    // El servidor responde con un booleano: true si hay datos
                    if (input.readBoolean()) {
                        val size = input.readLong().toInt()
                        Log.d(TAG, "Tamaño esperado: $size bytes")
                        val buffer = ByteArray(size)
                        input.readFully(buffer)  // lee exactamente 'size' bytes

                        // Enviar confirmación ACK para que el servidor cierre bien
                        out.writeUTF("ACK")
                        out.flush()

                        Log.d(TAG, "Descarga completada.")
                        resultado = buffer
                    } else {
                        Log.w(TAG, "No existe DNI en el servidor.")
                    }
                }
            }.getOrElse { e ->
                Log.e(TAG, "Error en download: ${e.message}", e)
                // resultado ya es null
            }
            resultado  // único punto de retorno
        }
}
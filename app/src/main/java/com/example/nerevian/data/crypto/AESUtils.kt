package com.example.nerevian.data.crypto

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AESUtils {
    /**
     * Usamos el modo de transformación completo: Algoritmo/Modo/Padding.
     * PKCS5Padding es esencial para evitar el error de "last block incomplete",
     * ya que rellena automáticamente el bloque final del archivo para que sea múltiplo de 16 bytes.
     */
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
    private const val ALGORITHM = "AES"

    // ⚠️ Esta clave de 16 caracteres (128 bits) debe coincidir en servidor y app.
    private const val SECRET_KEY = "DniSecretKey26!X"

    @Throws(Exception::class)
    fun getEncryptCipher(): Cipher {
        return getCipher(Cipher.ENCRYPT_MODE)
    }

    @Throws(Exception::class)
    fun getDecryptCipher(): Cipher {
        return getCipher(Cipher.DECRYPT_MODE)
    }

    @Throws(Exception::class)
    private fun getCipher(mode: Int): Cipher {
        // Convertimos el String de la clave a bytes usando UTF-8 para evitar caracteres extraños.
        val keyBytes = SECRET_KEY.toByteArray(Charsets.UTF_8)

        // Creamos la especificación de la llave indicando que es para el algoritmo AES.
        val secretKey = SecretKeySpec(keyBytes, ALGORITHM)

        // Obtenemos la instancia del Cipher con la transformación completa definida arriba.
        val cipher = Cipher.getInstance(TRANSFORMATION)

        // Inicializamos el motor de cifrado/descifrado con la llave configurada.
        cipher.init(mode, secretKey)

        return cipher
    }
}
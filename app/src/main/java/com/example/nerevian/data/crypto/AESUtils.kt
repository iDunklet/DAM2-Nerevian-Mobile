package com.example.nerevian.data.crypto

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Utilidad AES para cifrar/descifrar imágenes (compatible con el servidor).
 */
object AESUtils {

    // ==================== CONSTANTES ====================
    // Transformación completa (algoritmo/modo/relleno). PKCS5Padding rellena el último bloque.
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
    private const val ALGORITHM = "AES"
    private const val SECRET_KEY = "DniSecretKey26!X"  // 16 bytes (AES-128)

    // ==================== MÉTODOS PÚBLICOS ====================

    @Throws(Exception::class)
    fun getEncryptCipher(): Cipher = getCipher(Cipher.ENCRYPT_MODE)

    @Throws(Exception::class)
    fun getDecryptCipher(): Cipher = getCipher(Cipher.DECRYPT_MODE)

    // ==================== MÉTODO PRIVADO ====================

    @Throws(Exception::class)
    private fun getCipher(mode: Int): Cipher {
        // Clave en bytes con UTF-8 (mismo encoding que servidor)
        val keyBytes = SECRET_KEY.toByteArray(Charsets.UTF_8)
        val secretKey = SecretKeySpec(keyBytes, ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(mode, secretKey)
        return cipher
    }
}
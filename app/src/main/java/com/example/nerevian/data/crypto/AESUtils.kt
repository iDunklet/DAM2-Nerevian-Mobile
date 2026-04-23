package com.example.nerevian.data.crypto

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AESUtils {
    private const val ALGORITHM = "AES"

    // ⚠️ ATENCIÓN: Esta clave DEBE ser exactamente la misma que
    // pusiste en la variable AES_KEY de Dokploy para que el espejo funcione.
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
        // En Kotlin es buena práctica especificar UTF_8 al pasar a bytes
        val keyBytes = SECRET_KEY.toByteArray(Charsets.UTF_8)
        val secretKey = SecretKeySpec(keyBytes, ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(mode, secretKey)
        return cipher
    }
}
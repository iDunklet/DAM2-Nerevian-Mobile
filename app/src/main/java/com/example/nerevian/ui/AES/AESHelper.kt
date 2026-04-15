package com.example.nerevian.ui.AES

import android.content.Context
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object AESHelper {
    private const val ALGO = "AES"
    private const val PREFS_NAME = "DniSecurity"
    private const val KEY_NAME = "my_dni_key"

    // 1. 生成并保存钥匙
    fun getOrCreateKey(context: Context): SecretKey {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedKey = prefs.getString(KEY_NAME, null)

        return if (savedKey == null) {
            val keyGen = KeyGenerator.getInstance(ALGO)
            keyGen.init(256)
            val newKey = keyGen.generateKey()
            val encodedKey = Base64.encodeToString(newKey.encoded, Base64.DEFAULT)
            prefs.edit().putString(KEY_NAME, encodedKey).apply()
            newKey
        } else {
            val decodedKey = Base64.decode(savedKey, Base64.DEFAULT)
            SecretKeySpec(decodedKey, 0, decodedKey.size, ALGO)
        }
    }

    // 2. 加密字节
    fun encrypt(data: ByteArray, key: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(ALGO)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(data)
    }

    // 3. 解密字节
    fun decrypt(data: ByteArray, key: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(ALGO)
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(data)
    }
}
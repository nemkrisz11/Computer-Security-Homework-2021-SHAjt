package com.shajt.caffshop.utils

import android.security.keystore.KeyProperties
import android.security.keystore.KeyGenParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Based on: https://gist.github.com/JosiasSena/3bf4ca59777f7dedcaf41a495d96d984
 */
class EnCryptor {

    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    }

    lateinit var encryption: ByteArray
        private set
    lateinit var iv: ByteArray
        private set

    fun encryptText(alias: String, textToEncrypt: String): ByteArray {
        val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias))
        iv = cipher.iv
        return cipher.doFinal(textToEncrypt.toByteArray(Charsets.UTF_8)).also {
            encryption = it
        }
    }

    fun getSecretKey(alias: String): SecretKey {
        val keyGenerator: KeyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false) // TODO
                .build()
        )
        return keyGenerator.generateKey()
    }
}
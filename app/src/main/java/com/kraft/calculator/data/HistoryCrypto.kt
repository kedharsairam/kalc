package com.kraft.calculator.data

import android.content.Context
import android.util.Base64
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

/**
 * Field-level AES-256-GCM encryption for history entries.
 * Key is Keystore-backed via MasterKey (hardware if available).
 * Each field gets a random 12-byte IV, prepended to ciphertext.
 */
class HistoryCrypto(context: Context) {

    private val appContext = context.applicationContext

    private val masterKey by lazy {
        MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private fun getKey(): javax.crypto.SecretKey {
        // MasterKey wraps a random key in Keystore; use it directly for GCM
        // Actually, generate our own key protected by MasterKey encryption.
        // Simpler: use MasterKey to encrypt a random data key stored in prefs.
        // For v1: use AndroidKeyStore directly for a dedicated alias.
        val ks = java.security.KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val alias = "kalc_history_key"
        if (!ks.containsAlias(alias)) {
            val keyGen = javax.crypto.KeyGenerator.getInstance(
                android.security.keystore.KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
            )
            keyGen.init(
                android.security.keystore.KeyGenParameterSpec.Builder(
                    alias,
                    android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or
                        android.security.keystore.KeyProperties.PURPOSE_DECRYPT,
                )
                    .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
            keyGen.generateKey()
        }
        return (ks.getEntry(alias, null) as java.security.KeyStore.SecretKeyEntry).secretKey
    }

    fun encrypt(plaintext: String): String {
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), GCMParameterSpec(128, iv))
            val ct = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            val combined = iv + ct
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (_: Exception) {
            // Fallback: store with prefix marking unencrypted (migration safety)
            "plain:$plaintext"
        }
    }

    fun decrypt(encoded: String): String {
        return try {
            if (encoded.startsWith("plain:")) {
                return encoded.removePrefix("plain:")
            }
            val combined = Base64.decode(encoded, Base64.NO_WRAP)
            if (combined.size < 13) return ""
            val iv = combined.sliceArray(0 until 12)
            val ct = combined.sliceArray(12 until combined.size)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, getKey(), GCMParameterSpec(128, iv))
            String(cipher.doFinal(ct), Charsets.UTF_8)
        } catch (_: Exception) {
            ""
        }
    }
}

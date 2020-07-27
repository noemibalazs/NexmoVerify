package com.example.nexmoverify.otp

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Base64.encodeToString
import com.example.nexmoverify.util.HASH_TYPE
import com.example.nexmoverify.util.NUM_BASE64_CHAR
import com.example.nexmoverify.util.NUM_HASHED_BYTES
import com.orhanobut.logger.Logger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

class AppSignatureHelper(val context: Context) : ContextWrapper(context) {

    fun getAppSignature(): ArrayList<String> {
        val signatureList = arrayListOf<String>()
        try {
            val packageName = packageName
            val packageManager = packageManager
            val signatures =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
            signatures.forEach { signature ->
                signature?.let {
                    val hashString = hash(packageName, it.toCharsString())
                    hashString?.let { hash ->
                        signatureList.add(hash)
                    }
                }
            }
        } catch (e: Exception) {
            Logger.e("getAppSignature error message: ${e.message}")
        }
        return signatureList
    }

    private fun hash(packageName: String, signature: String): String? {
        var base64Hash = ""
        val info = "$packageName $signature"
        try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)
            messageDigest.update(info.toByteArray(StandardCharsets.UTF_8))
            var hashSignature = messageDigest.digest()
            hashSignature = Arrays.copyOfRange(hashSignature, 0,
                NUM_HASHED_BYTES
            )
            base64Hash = encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
            return base64Hash.substring(0,
                NUM_BASE64_CHAR
            )

        } catch (e: Exception) {
            Logger.e("hash error: ${e.message}")
        }
        return base64Hash
    }
}
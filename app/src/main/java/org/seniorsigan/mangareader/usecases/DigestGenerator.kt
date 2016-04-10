package org.seniorsigan.mangareader.usecases

import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class DigestGenerator {
    fun digest(s: String): String {
        return md5(s)
    }

    private fun md5(s: String): String {
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance(MD5)
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2)
                    h = "0" + h
                hexString.append(h)
            }
            return hexString.toString()

        } catch (e: NoSuchAlgorithmException) {
            Log.e("DigestGenerator", "${e.message}", e)
        }

        return ""
    }
}

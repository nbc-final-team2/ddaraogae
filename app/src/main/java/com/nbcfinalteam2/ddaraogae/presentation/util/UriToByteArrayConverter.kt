package com.nbcfinalteam2.ddaraogae.presentation.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

object UriToByteArrayConverter {
    private const val TAG = "UriToByteArrayConvertor"

    fun uriToByteArray(uri: Uri?, context: Context): ByteArray? {
        return try {
            val inputStream: InputStream? = uri?.let { context.contentResolver.openInputStream(it) }
            inputStream?.use { toByteArray(it) }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to convert URI to ByteArray", e)
            null
        }
    }

    private fun toByteArray(inputStream: InputStream): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        byteArrayOutputStream.write(inputStream.readBytes())
        return byteArrayOutputStream.toByteArray()
    }
}
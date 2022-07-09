package com.clientai.recog.digital

import android.util.Log
import com.google.common.io.Files
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

object UploadUtils {
    private const val TAG = "ClientAI#upload"
    private const val UPLOAD_SERVER_PREFIX = "http://129.204.41.76:8000"
    private const val UPLOAD_TAG = "digital"
    private var okHttpClient: OkHttpClient? = null
    private fun getOkHttp(): OkHttpClient? {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder().build()
        }
        return okHttpClient
    }

    private fun readFile(file: File): ByteArray? {
        try {
            return Files.toByteArray(file)
        } catch (e: IOException) {
            Log.e(TAG, "readFile error", e)
        }
        return null
    }

    fun uploadFile(file: String, name: String) {
        val url = "/upload/$UPLOAD_TAG?filename=$name"
        val bodyBytes = readFile(File(file)) ?: return
        val body = bodyBytes.toRequestBody("image/png".toMediaTypeOrNull())
        val request: Request = Request.Builder()
            .url(UPLOAD_SERVER_PREFIX + url)
            .post(body)
            .build()
        getOkHttp()?.let {
            it.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.w(TAG, "uploadFile error", e)
                    }
                    override fun onResponse(call: Call, response: Response) {
                        Log.d(TAG, "uploadFile success response:${response.body}")
                    }
                })
        }
    }
}
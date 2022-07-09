package com.clientai.recog.digital

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat


object BitmapUtils {
    private const val TAG = "ClientAI#bitmap"
    private const val saveSize = 128 // 保存图片大小

    fun saveBitmap(bitmap: Bitmap, label: String, context: Context): Boolean {
        val saveDir = context.getExternalFilesDir("Track/${label}") ?: return false
        if (!saveDir.exists()) {
            val result = saveDir.mkdirs()
            Log.d(TAG, "saveBitmap mkdirs result=$result")
        }
        val now = System.currentTimeMillis()
        val timeString = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(now)
        val name = "${timeString}_${now%1000}.png"
        val saveFile = File(saveDir.absolutePath, name)
        try {
            val saveImgOut = FileOutputStream(saveFile)
            val resizedImage = Bitmap.createScaledBitmap(bitmap, saveSize, saveSize, true)
            resizedImage.compress(Bitmap.CompressFormat.PNG, 80, saveImgOut)
            saveImgOut.flush()
            saveImgOut.close()
            Log.d(TAG, "saveBitmap success, path=${saveFile.absolutePath}")

            UploadUtils.uploadFile(saveFile.absolutePath, "$label/$name") // 上传到服务端

            return true
        } catch (ex: IOException) {
            Log.e(TAG, "saveBitmap error!", ex)
        }
        return false
    }
}
package com.agronify.android.util

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import com.agronify.android.R
import com.agronify.android.util.DateUtil.getCurrentTime
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

object CameraUtil {
    private const val MAXIMUM_IMAGE_SIZE = 1024 * 1024 * 5
    private const val RESIZE_WIDTH = 300
    private const val RESIZE_HEIGHT = 400
    private const val COMPRESS_QUALITY = 100

    fun createTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("agronify-" + getCurrentTime(), ".jpg", storageDir)
    }

    fun createFile(application: Application): File {
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        val outputDirectory = if (
            mediaDir != null && mediaDir.exists()
        ) mediaDir else application.filesDir

        return File(outputDirectory, "agronify-" + getCurrentTime() + ".jpg")
    }

    fun rescaleFile(file: File, imageRotationDegree: Int = 0) {
        val matrix = Matrix()
        val bitmap = BitmapFactory.decodeFile(file.path)

        val rotation = when (imageRotationDegree) {
            0 -> 90f
            180 -> 270f
            270 -> 180f
            else -> 0f
        }
        matrix.postRotate(rotation)

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, RESIZE_WIDTH, RESIZE_HEIGHT, true)
        val result = Bitmap.createBitmap(resizedBitmap, 0, 0, RESIZE_WIDTH, RESIZE_HEIGHT, matrix, true)

        val outputStream = FileOutputStream(file)
        result.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    fun uriToFile(selected: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val file = createTempFile(context)

        val inputStream = contentResolver.openInputStream(selected) as InputStream
        val outputStream = FileOutputStream(file) as OutputStream
        val buf = ByteArray(1024)
        var len: Int

        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return file
    }

    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = COMPRESS_QUALITY
        var streamLength: Int

        do {
            val bitmapStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bitmapStream)
            val bitmapBytes = bitmapStream.toByteArray()
            streamLength = bitmapBytes.size
            compressQuality -= 5
        } while (streamLength >= MAXIMUM_IMAGE_SIZE)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

        return file
    }
}
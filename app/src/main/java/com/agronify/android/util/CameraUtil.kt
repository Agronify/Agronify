package com.agronify.android.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.os.Environment
import android.util.Size
import com.agronify.android.util.DateUtil.getCurrentTime
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

object CameraUtil {
    private const val COMPRESS_QUALITY = 100
    private const val MAXIMUM_SIZE = 1000000

    fun createTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("agronify-" + getCurrentTime(), ".jpg", storageDir)
    }

    fun processFileCamera(file: File, imageRotationDegree: Int) {
        val matrix = Matrix()
        matrix.postRotate(rotateImage(imageRotationDegree))
        val bitmap = BitmapFactory.decodeFile(file.path)
        val outputStream = FileOutputStream(file)

        if (bitmap.height > 960 || bitmap.width > 1280) {
            val resizedBitmap = resizeImage(bitmap)
            val croppedBitmap = cropImage(resizedBitmap)

            val result = Bitmap.createBitmap(croppedBitmap, 0, 0, croppedBitmap.width, croppedBitmap.height, matrix, true)
            result.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, FileOutputStream(file))
        } else if (bitmap.height < 800 || bitmap.width < 600) {
            val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            result.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, FileOutputStream(file))
        } else {
            val croppedBitmap = cropImage(bitmap)

            val result = Bitmap.createBitmap(croppedBitmap, 0, 0, croppedBitmap.width, croppedBitmap.height, matrix, true)
            result.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, FileOutputStream(file))
        }

        outputStream.flush()
        outputStream.close()
    }

    fun processFileGallery(file: File, imageRotationDegree: Int) {
        val matrix = Matrix()
        matrix.postRotate(rotateImage(imageRotationDegree))
        val bitmap = BitmapFactory.decodeFile(file.path)
        val outputStream = FileOutputStream(file)

        val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        result.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, FileOutputStream(file))

        outputStream.flush()
        outputStream.close()
    }

    private fun resizeImage(bitmap: Bitmap): Bitmap {
        val desiredHeight = 960
        val scale = bitmap.height.toFloat() / desiredHeight
        val width = (bitmap.width.toFloat() / scale).toInt()
        val height = (bitmap.height.toFloat() / scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun rotateImage(rotation: Int): Float {
        return when (rotation) {
            0 -> 90f
            180 -> 270f
            270 -> 180f
            else -> 0f
        }
    }

    private fun cropImage(bitmap: Bitmap): Bitmap {
        val placeholder = Size(800, 600)
        val placeholderX = (bitmap.width - placeholder.width) / 2
        val placeholderY = (bitmap.height - placeholder.height) / 2

        val crop = Rect(placeholderX, placeholderY, placeholderX + placeholder.width, placeholderY + placeholder.height)
        return Bitmap.createBitmap(bitmap, crop.left, crop.top, crop.width(), crop.height())
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

    fun reduceFile(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = COMPRESS_QUALITY
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray: ByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength >= MAXIMUM_SIZE)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

        return file
    }
}
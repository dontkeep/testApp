package com.exal.testapp.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun File.compressFile(context: Context): File? {
    try {
        // Step 1: Convert the file to Bitmap
        val bitmap = BitmapFactory.decodeFile(this.path)

        if (bitmap == null) {
            Log.e("compressFile", "Failed to decode file into Bitmap.")
            return null
        }

        // Step 2: Reduce the Bitmap size by compressing it
        val compressedFile = this.reduceFileImage(bitmap, context)

        // Step 3: Save the compressed Bitmap to the file
        val outputStream = FileOutputStream(compressedFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // 80% quality
        outputStream.flush()
        outputStream.close()

        return compressedFile
    } catch (e: Exception) {
        Log.e("compressFile", "Error compressing file: ${e.message}")
        return null
    }
}

fun File.reduceFileImage(bitmap: Bitmap, context: Context): File {
    val MAXIMAL_SIZE = 2 * 1024 * 1024 // 2MB
    var compressQuality = 100
    var streamLength: Int
    val bmpStream = ByteArrayOutputStream()

    // Compressing the bitmap until it's under the 2MB size
    do {
        bmpStream.reset()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)

    val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) // You can use INTERNAL_STORAGE if needed
    val compressedFile = File(outputDir, "compressed_image_${System.currentTimeMillis()}.jpg")

    // Saving the compressed bitmap back to the new file
    val outputStream = FileOutputStream(compressedFile)
    outputStream.write(bmpStream.toByteArray())
    outputStream.flush()
    outputStream.close()

    return compressedFile
}


fun Bitmap.getRotatedBitmap(file: File): Bitmap {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}
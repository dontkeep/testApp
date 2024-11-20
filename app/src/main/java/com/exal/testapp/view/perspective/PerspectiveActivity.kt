package com.exal.testapp.view.perspective

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.exal.testapp.databinding.ActivityPerspectiveBinding
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.FileNotFoundException
import kotlin.math.pow

class PerspectiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerspectiveBinding
    private var originalBitmap: Bitmap? = null
    private var transformedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerspectiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!OpenCVLoader.initDebug()) {
            finish()
        }

        binding.transformButton.setOnClickListener {
            performPerspectiveTransform()
            transformedBitmap?.let {
                binding.imageView.setImageBitmap(it)
            }
        }

        binding.galleryBtn.setOnClickListener {
            selectImageFromGallery()
        }

        binding.saveBtn.setOnClickListener {
            transformedBitmap?.let {
                saveImageToGallery(it)
            } ?: run {
                Toast.makeText(this, "Transformasi belum dilakukan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                try {
                    val inputStream = contentResolver.openInputStream(imageUri)
                    originalBitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageView.setImageBitmap(originalBitmap)
                    binding.edtView.setBitmap(originalBitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun performPerspectiveTransform() {
        val points = binding.edtView.getPerspective()
        if (points != null && originalBitmap != null) {
            transformedBitmap = transformBitmap(originalBitmap!!, points)
        }
    }

    private fun transformBitmap(bitmap: Bitmap, points: PerspectivePoints): Bitmap {
        // Hitung rasio baru berdasarkan perspektif
        val width = calculateDistance(points.pointLeftTop, points.pointRightTop).toInt()
        val height = calculateDistance(points.pointLeftTop, points.pointLeftBottom).toInt()

        // Titik sumber (perspektif yang diedit pengguna)
        val src = MatOfPoint2f(
            Point(points.pointLeftTop.x.toDouble(), points.pointLeftTop.y.toDouble()),
            Point(points.pointRightTop.x.toDouble(), points.pointRightTop.y.toDouble()),
            Point(points.pointRightBottom.x.toDouble(), points.pointRightBottom.y.toDouble()),
            Point(points.pointLeftBottom.x.toDouble(), points.pointLeftBottom.y.toDouble())
        )

        // Titik tujuan (hasil transformasi dengan rasio baru)
        val dst = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(width.toDouble(), 0.0),
            Point(width.toDouble(), height.toDouble()),
            Point(0.0, height.toDouble())
        )

        // Transformasi perspektif
        val transformMatrix = Imgproc.getPerspectiveTransform(src, dst)

        val srcMat = Mat()
        Utils.bitmapToMat(bitmap, srcMat)

        val dstMat = Mat()
        Imgproc.warpPerspective(srcMat, dstMat, transformMatrix, Size(width.toDouble(), height.toDouble()))

        // Konversi kembali ke Bitmap
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(dstMat, outputBitmap)

        return outputBitmap
    }

    // Fungsi untuk menghitung jarak antara dua titik
    private fun calculateDistance(p1: PointF, p2: PointF): Float {
        return kotlin.math.sqrt((p2.x - p1.x).pow(2) + (p2.y - p1.y).pow(2))
    }

    private fun saveImageToGallery(bitmap: Bitmap): Boolean {
        return try {
            // Membuat nama file unik
            val filename = "perspective_edit_${System.currentTimeMillis()}.jpg"

            // Mendapatkan direktori gambar di penyimpanan perangkat
            val resolver = contentResolver
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/PerspectiveEditor")
            }

            // Menyimpan gambar ke galeri
            val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                val outputStream = resolver.openOutputStream(it)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
            }

            // Menampilkan pesan sukses
            Toast.makeText(this, "Gambar berhasil disimpan!", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            // Menampilkan pesan error
            Toast.makeText(this, "Gagal menyimpan gambar!", Toast.LENGTH_SHORT).show()
            false
        }
    }


    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1
    }
}

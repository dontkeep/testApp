package com.exal.testapp.view.perspective

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.exal.testapp.databinding.ActivityPerspectiveBinding
import com.exal.testapp.view.editlist.EditListActivity
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Izin menyimpan gambar diperlukan!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerspectiveBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.perspectiveActivity) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(this, "OpenCV tidak dapat dimuat", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.transformBtn.setOnClickListener {
            performPerspectiveTransform()
            transformedBitmap?.let {
                binding.imageView.setImageBitmap(it)
            }
        }

        binding.nextBtn.setOnClickListener {
            val intent = Intent(this, EditListActivity::class.java)
            startActivity(intent)
        }

        val imageUri = intent.getStringExtra(EXTRA_CAMERAX_IMAGE)?.toUri()
        Log.d("PerspectiveActivity", "Image URI Camera: $imageUri")
        if (imageUri != null) {
            loadBitmapFromUri(imageUri)
        } else {
            Toast.makeText(this, "Path gambar tidak tersedia!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            loadBitmapFromUri(uri)
        } else {
            Log.d("PerspectiveActivity", "Tidak ada gambar yang dipilih")
        }
    }

    private fun loadBitmapFromUri(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            originalBitmap = BitmapFactory.decodeStream(inputStream)
            if (originalBitmap != null && originalBitmap!!.width > 0 && originalBitmap!!.height > 0) {
                binding.imageView.setImageBitmap(originalBitmap)
                binding.edtView.setBitmap(originalBitmap)
                Log.d("PerspectiveActivity", "Image Uri : $uri")
                Log.d("PerspectiveActivity", "Width dan height bitmap: ${originalBitmap!!.width} x ${originalBitmap!!.height}")
                Log.d("PerspectiveActivity", "Bitmap : $originalBitmap")
                Log.d("PerspectiveActivity", "Bitmap berhasil dimuat")
            } else {
                throw IllegalArgumentException("Bitmap memiliki ukuran width/height 0")
            }
        } catch (e: FileNotFoundException) {
            Log.e("PerspectiveActivity", "File gambar tidak ditemukan: ${e.message}")
            Toast.makeText(this, "Gagal memuat gambar!", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: IllegalArgumentException) {
            Log.e("PerspectiveActivity", "Bitmap tidak valid: ${e.message}")
            Toast.makeText(this, "Gambar tidak valid!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun performPerspectiveTransform() {
        val points = binding.edtView.getPerspective()
        Log.d("PerspectiveActivity", "Points: $points")

        if (points == null) {
            Toast.makeText(this, "Titik perspektif tidak valid!", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidPoints(points)) {
            Toast.makeText(this, "Titik perspektif memiliki nilai yang tidak valid!", Toast.LENGTH_SHORT).show()
            return
        }

        if (originalBitmap != null) {
            transformedBitmap = transformBitmap(originalBitmap!!, points)
        }
    }

    private fun isValidPoints(points: PerspectivePoints): Boolean {
        val uniquePoints = setOf(
            points.pointLeftTop,
            points.pointRightTop,
            points.pointLeftBottom,
            points.pointRightBottom
        )
        return uniquePoints.size == 4
    }

    private fun transformBitmap(bitmap: Bitmap, points: PerspectivePoints): Bitmap {
        val width = calculateDistance(points.pointLeftTop, points.pointRightTop).toInt()
        val height = calculateDistance(points.pointLeftTop, points.pointLeftBottom).toInt()

        val src = MatOfPoint2f(
            Point(points.pointLeftTop.x.toDouble(), points.pointLeftTop.y.toDouble()),
            Point(points.pointRightTop.x.toDouble(), points.pointRightTop.y.toDouble()),
            Point(points.pointRightBottom.x.toDouble(), points.pointRightBottom.y.toDouble()),
            Point(points.pointLeftBottom.x.toDouble(), points.pointLeftBottom.y.toDouble())
        )

        val dst = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(width.toDouble(), 0.0),
            Point(width.toDouble(), height.toDouble()),
            Point(0.0, height.toDouble())
        )

        val transformMatrix = Imgproc.getPerspectiveTransform(src, dst)

        val srcMat = Mat()
        Utils.bitmapToMat(bitmap, srcMat)

        val dstMat = Mat()
        Imgproc.warpPerspective(
            srcMat,
            dstMat,
            transformMatrix,
            Size(width.toDouble(), height.toDouble())
        )

        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(dstMat, outputBitmap)

        return outputBitmap
    }

    private fun calculateDistance(p1: android.graphics.PointF, p2: android.graphics.PointF): Float {
        return kotlin.math.sqrt((p2.x - p1.x).pow(2) + (p2.y - p1.y).pow(2))
    }

    private fun saveImageToGallery(bitmap: Bitmap): Boolean {
        return try {
            checkAndRequestPermission()

            val resolver = contentResolver
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, "perspective_${System.currentTimeMillis()}.jpg")
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/PerspectiveEditor")
            }

            val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
            }
            Toast.makeText(this, "Gambar berhasil disimpan!", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            Log.e("PerspectiveActivity", "Gagal menyimpan gambar: ${e.message}")
            Toast.makeText(this, "Gagal menyimpan gambar!", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun checkAndRequestPermission() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    companion object {
        const val EXTRA_CAMERAX_IMAGE = "extra_camerax_image"
    }
}

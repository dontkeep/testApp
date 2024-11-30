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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.exal.testapp.data.Resource
import com.exal.testapp.databinding.ActivityPerspectiveBinding
import com.exal.testapp.helper.compressFile
import com.exal.testapp.helper.createCustomTempFile
import com.exal.testapp.helper.reduceFileImage
import com.exal.testapp.view.editlist.EditListActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import kotlin.math.pow

@AndroidEntryPoint
class PerspectiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerspectiveBinding
    private val viewModel: PerspectiveViewModel by viewModels()

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

        if (OpenCVLoader.initDebug()) {
            Log.d("PerspectiveActivity", "OpenCV initialization succeeded")
        } else {
            Log.e("PerspectiveActivity", "OpenCV initialization failed")
        }

        observeViewModel()

        val imageUri = intent.getStringExtra(EXTRA_CAMERAX_IMAGE)?.toUri()
        if (imageUri != null) {
            loadBitmapFromUri(imageUri)
        } else {
            Toast.makeText(this, "Path gambar tidak tersedia!", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.transformBtn.setOnClickListener {
            val points = binding.edtView.getPerspective()
            if (points == null || !viewModel.isValidPoints(points)) {
                Toast.makeText(this, "Titik perspektif tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.setPerspectivePoints(
                points.pointLeftTop,
                points.pointRightTop,
                points.pointRightBottom,
                points.pointLeftBottom
            )
            viewModel.performPerspectiveTransform()

            viewModel.transformedBitmap.observe(this) { transformedBitmap ->
                if (transformedBitmap != null) {
                    binding.imageView.setImageBitmap(transformedBitmap) // Display transformed image
                    Toast.makeText(this, "Transformasi selesai!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Transformasi gagal!", Toast.LENGTH_SHORT).show()
                }
            }
            with(binding){
                nextBtn.isEnabled = true
                nextBtn.text = "Next"
            }
        }

        binding.nextBtn.setOnClickListener {
            val transformedBitmap = viewModel.transformedBitmap.value
            if (transformedBitmap != null) {
                val file = bitmapToFile(transformedBitmap)
                val compressedFile = file?.compressFile(this)
                if (compressedFile != null) {
                    val filePart = MultipartBody.Part.createFormData(
                        "file", compressedFile.name, compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                    viewModel.scanImage(filePart)
                } else {
                    Toast.makeText(this, "Gagal memproses gambar!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Tidak ada gambar untuk dipindai!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.scanState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Toast.makeText(this, "Memindai gambar...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    val scanResponse = resource.data
                    if (scanResponse != null) {
                        val intent = Intent(this, EditListActivity::class.java)
                        intent.putExtra("SCAN_DATA", scanResponse)
                        startActivity(intent)
                    }
                }
                is Resource.Error -> {
                    Log.d("PerspectiveActivity", "Error: ${resource.message}")
                    Toast.makeText(this, "Gagal memindai: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap): File? {
        return try {
            val file = File(cacheDir, "transformed_image.jpg")
            var outputStream: FileOutputStream

            var quality = 100
            var bitmapCompressed = bitmap
            while (true) {
                outputStream = file.outputStream()
                bitmapCompressed.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.flush()
                outputStream.close()

                if (file.length() <= 2 * 1024 * 1024) {
                    break
                }
                quality -= 10
                if (quality <= 10) break
            }
            file
        } catch (e: Exception) {
            Log.e("PerspectiveActivity", "Error saving bitmap to file: ${e.message}")
            null
        }
    }


    private fun observeViewModel() {
        viewModel.originalBitmap.observe(this) { bitmap ->
            if (bitmap != null) {
                binding.imageView.setImageBitmap(bitmap)
                binding.edtView.setBitmap(bitmap)
            }
        }

        viewModel.transformedBitmap.observe(this) { bitmap ->
            if (bitmap != null) {
                binding.imageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun loadBitmapFromUri(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (bitmap != null && bitmap.width > 0 && bitmap.height > 0) {
                viewModel.setOriginalBitmap(bitmap)
            } else {
                throw IllegalArgumentException("Bitmap memiliki ukuran width/height 0")
            }
        } catch (e: Exception) {
            Log.e("PerspectiveActivity", "Gagal memuat gambar: ${e.message}")
            Toast.makeText(this, "Gagal memuat gambar!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {
        const val EXTRA_CAMERAX_IMAGE = "extra_camerax_image"
    }
}

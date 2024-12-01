package com.exal.testapp.view.perspective

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.exal.testapp.data.Resource
import com.exal.testapp.databinding.ActivityPerspectiveBinding
import com.exal.testapp.helper.compressFile
import com.exal.testapp.view.editlist.EditListActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.opencv.android.OpenCVLoader
import java.io.File
import java.io.FileOutputStream

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
                showSnackbar("Titik perspektif tidak valid!")
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
                    showSnackbar("Transformasi selesai!")
                } else {
                    showSnackbar("Transformasi gagal!")
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
                    showSnackbar("Gagal memproses gambar!")
                }
            } else {
                showSnackbar("Tidak ada gambar untuk dipindai!")
            }
        }

        viewModel.scanState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                    showSnackbar("Memindai gambar...")
                }
                is Resource.Success -> {
                    val scanResponse = resource.data
                    if (scanResponse != null) {
                        showLoading(false)
                        val intent = Intent(this, EditListActivity::class.java)
                        intent.putExtra("SCAN_DATA", scanResponse)
                        startActivity(intent)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Log.d("PerspectiveActivity", "Error: ${resource.message}")
                    showSnackbar("Gagal memindai: ${resource.message}")
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

    private fun showLoading(b: Boolean) {
        binding.progressIndicator.visibility = if (b) View.VISIBLE else View.GONE
        binding.nextBtn.isEnabled = !b
        binding.transformBtn.isEnabled = !b
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.perspectiveActivity, message, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.nextBtn)
            .show()
    }

    companion object {
        const val EXTRA_CAMERAX_IMAGE = "extra_camerax_image"
    }
}

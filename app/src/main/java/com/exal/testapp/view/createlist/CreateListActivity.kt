package com.exal.testapp.view.createlist

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.MainActivity
import com.exal.testapp.R
import com.exal.testapp.data.Resource
import com.exal.testapp.data.network.response.PostListResponse
import com.exal.testapp.data.network.response.ProductsItem
import com.exal.testapp.data.request.ProductItem
import com.exal.testapp.databinding.ActivityCreateListBinding
import com.exal.testapp.helper.formatRupiah
import com.exal.testapp.view.adapter.ItemAdapter
import com.exal.testapp.view.camera.CameraActivity
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class CreateListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateListBinding
    private var receiptImagePath: String? = null
    private var thumbnailImagePath: String? = null

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }

    private val viewModel: CreateListViewModel by viewModels()

    private val requestPermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val grantedPermissions = permissions.filterValues { it }
            val deniedPermissions = permissions.filterValues { !it }

            if (grantedPermissions.isNotEmpty()) {
                Toast.makeText(this, R.string.permission_request_granted, Toast.LENGTH_LONG).show()
            }
            if (deniedPermissions.isNotEmpty()) {
                Toast.makeText(this, R.string.permission_request_denied, Toast.LENGTH_LONG).show()
            }
        }


    private fun allPermissionsGranted(): Boolean {
        val cameraGranted = ContextCompat.checkSelfPermission(
            this, REQUIRED_CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

        val mediaGranted = ContextCompat.checkSelfPermission(
            this, REQUIRED_MEDIA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

        return cameraGranted && mediaGranted
    }


    private var clicked = false

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imageUri = intent.getStringExtra("IMAGE_URI")

        if (imageUri != null) {
            val uri = Uri.parse(imageUri)
            viewModel.setImageUri(uri.toString())
            viewModel.imageUri.observe(this) { image ->
                binding.imageView.setImageURI(image.toUri())
            }
        } else {
            Log.e("CreateListActivity", "Path gambar tidak tersedia!")
        }

        if(viewModel.productList.value?.isEmpty() == true && viewModel.totalPrice.value == 0) {
            saveReceivedData()
        }

        rvSetup()

        binding.fabBottomAppBar.setOnClickListener {
            onAddButtonClick()
        }

        binding.fabScanReceipt.setOnClickListener {
            if (!allPermissionsGranted()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requestPermissionsLauncher.launch(
                        arrayOf(REQUIRED_MEDIA_SELECTED, REQUIRED_MEDIA_PERMISSION, REQUIRED_CAMERA_PERMISSION)
                    )
                } else {

                    requestPermissionsLauncher.launch(
                        arrayOf(REQUIRED_EXTERNAL_STORAGE, REQUIRED_CAMERA_PERMISSION)
                    )
                }
            } else {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            }
        }

        binding.saveButton.setOnClickListener {
            handleSaveButtonClick()
        }


        binding.fabAddManual.setOnClickListener {
            Toast.makeText(this, "Add Manual", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSaveButtonClick() {
        lifecycleScope.launch {
            val titleRequestBody = createRequestBody(binding.textFieldTitle.editText?.text.toString())
            val typeRequestBody = createRequestBody("Track")
            val totalExpensesRequestBody = createRequestBody(viewModel.totalPrice.value.toString())
            val productItemsRequestBody = createRequestBody(
                Gson().toJson(
                    viewModel.productList.value?.map {
                        ProductItem(it.name, it.amount, it.price, it.detail?.category, it.totalPrice)
                    }
                )
            )
            val receiptImagePart = createImagePart("receipt_image", receiptImagePath)
            val thumbnailImagePart = createImagePart("thumbnail_image", thumbnailImagePath)

            viewModel.postData(
                titleRequestBody,
                receiptImagePart,
                thumbnailImagePart,
                productItemsRequestBody,
                typeRequestBody,
                totalExpensesRequestBody
            ).collect { resource ->
                handleResource(resource)
            }
        }
    }

    private fun handleResource(resource: Resource<PostListResponse>) {
        when (resource) {
            is Resource.Loading -> {
                Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
            }
            is Resource.Success -> {
                Toast.makeText(this, "List Saved", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            is Resource.Error -> {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createRequestBody(value: String?): okhttp3.RequestBody =
        value.orEmpty().toRequestBody("text/plain".toMediaTypeOrNull())

    private fun createImagePart(name: String, path: String?): MultipartBody.Part? {
        return path?.let {
            val file = File(it)
            MultipartBody.Part.createFormData(
                name, file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }
    }

    private fun saveReceivedData(){
        val productList: ArrayList<ProductsItem>? = intent.getParcelableArrayListExtra("PRODUCT_LIST")
        val price = intent.getIntExtra("PRICE", 0)
        if (productList != null) {
            viewModel.setProductList(productList.toList(), price)
        }
    }

    private fun rvSetup() {
        val adapter = ItemAdapter{ item ->
            viewModel.deleteProduct(item)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.itemRv.layoutManager = layoutManager
        binding.itemRv.adapter = adapter

        viewModel.productList.observe(this) {
            adapter.submitList(it)
        }
        viewModel.totalPrice.observe(this) { price ->
            binding.totalPriceTv.text = formatRupiah(price)
            Log.d("CreateListActivity", "Total Price: $price")
        }
    }

    private fun onAddButtonClick() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.fabScanReceipt.visibility = View.VISIBLE
            binding.fabAddManual.visibility = View.VISIBLE
        } else {
            binding.fabScanReceipt.visibility = View.INVISIBLE
            binding.fabAddManual.visibility = View.INVISIBLE
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.fabScanReceipt.startAnimation(fromBottom)
            binding.fabAddManual.startAnimation(fromBottom)
            binding.fabBottomAppBar.startAnimation(rotateOpen)
        } else {
            binding.fabScanReceipt.startAnimation(toBottom)
            binding.fabAddManual.startAnimation(toBottom)
            binding.fabBottomAppBar.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean) {
        if (!clicked) {
            binding.fabScanReceipt.isClickable = true
            binding.fabAddManual.isClickable = true
        } else {
            binding.fabScanReceipt.isClickable = false
            binding.fabAddManual.isClickable = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setProductList(emptyList(), 0)
    }

    companion object {
        private const val REQUIRED_CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val REQUIRED_MEDIA_PERMISSION = Manifest.permission.READ_MEDIA_IMAGES
        private const val REQUIRED_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val REQUIRED_MEDIA_SELECTED = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
    }
}

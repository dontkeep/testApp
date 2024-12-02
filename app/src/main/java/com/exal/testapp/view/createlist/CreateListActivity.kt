package com.exal.testapp.view.createlist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.R
import com.exal.testapp.data.network.response.ProductsItem
import com.exal.testapp.databinding.ActivityCreateListBinding
import com.exal.testapp.view.adapter.ItemAdapter
import com.exal.testapp.view.camera.CameraActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateListBinding

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

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, R.string.permission_request_granted, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, R.string.permission_request_denied, Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private var clicked = false

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

        saveReceivedData()

        rvSetup()

        binding.fabBottomAppBar.setOnClickListener {
            onAddButtonClick()
        }

        binding.fabScanReceipt.setOnClickListener {
            if (!allPermissionsGranted()) {
                requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            } else {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            }
        }

        binding.fabAddManual.setOnClickListener {
            Toast.makeText(this, "Add Manual", Toast.LENGTH_SHORT).show()
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

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}

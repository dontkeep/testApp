package com.exal.testapp.view.editlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.data.network.response.ProductsItem
import com.exal.testapp.data.network.response.ScanImageResponse
import com.exal.testapp.databinding.ActivityEditListBinding
import com.exal.testapp.view.adapter.EditListAdapter
import com.exal.testapp.view.createlist.CreateListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditListBinding
    private val editListViewModel: EditListViewModel by viewModels()
    private lateinit var editListAdapter: EditListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditListBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.scanResultActivity) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom)
            )

            insets
        }

        editListAdapter = EditListAdapter { updatedItem ->
            editListViewModel.updateItem(updatedItem)
        }

        binding.nextBtn.setOnClickListener {
            val productList = editListAdapter.currentList
            val totalPrice = productList.sumOf { (it.price ?: 0) * (it.amount ?: 0) }

            productList.forEach { productItem ->
                Log.d("ProductList", "Name: ${productItem.name}, Price: ${productItem.price}, Quantity: ${productItem.amount}, id: ${productItem.id}")
            }

            Log.d("ProductList", "Total Price: $totalPrice")
            navigateToCreateListActivity(productList)
        }

        binding.rvScanResult.layoutManager = LinearLayoutManager(this@EditListActivity)
        binding.rvScanResult.adapter = editListAdapter

        if (savedInstanceState == null) {
            val scanData = intent.getParcelableExtra<ScanImageResponse>("SCAN_DATA")
            editListViewModel.setScanData(scanData)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        editListViewModel.scanData.observe(this) { scanData ->
            if (scanData != null) {
                val productList = scanData.products?.filterNotNull() ?: emptyList()
                editListAdapter.submitList(productList)
            } else {
                Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToCreateListActivity(productList: List<ProductsItem>) {
        val intent = Intent(this, CreateListActivity::class.java)
        intent.putParcelableArrayListExtra("PRODUCT_LIST", ArrayList(productList))
        startActivity(intent)
    }
}

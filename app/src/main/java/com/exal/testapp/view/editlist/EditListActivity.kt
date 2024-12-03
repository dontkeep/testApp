package com.exal.testapp.view.editlist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
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

            // Gabungkan padding untuk status bar, navigation bar, dan keyboard
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, imeInsets.bottom) // Menangani keyboard
            )

            insets
        }

        val imageUri = intent.getStringExtra("IMAGE_URI")

        editListAdapter = EditListAdapter { updatedItem ->
            editListViewModel.updateItem(updatedItem)
        }

        binding.nextBtn.setOnClickListener {
            val productList = editListAdapter.currentList
            val totalPrice = productList.sumOf { (it.price ?: 0) * (it.amount ?: 0) }

            val intent = Intent(this, CreateListActivity::class.java)
            intent.putParcelableArrayListExtra("PRODUCT_LIST", ArrayList(productList))
            intent.putExtra("IMAGE_URI", imageUri.toString())
            intent.putExtra("PRICE", totalPrice)
            startActivity(intent)
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
}

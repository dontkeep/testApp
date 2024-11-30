package com.exal.testapp.view.editlist

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.data.Resource
import com.exal.testapp.data.network.response.ScanImageResponse
import com.exal.testapp.databinding.ActivityEditListBinding
import com.exal.testapp.view.adapter.EditListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditListBinding
    private val editListViewModel: EditListViewModel by viewModels()
    private lateinit var editListAdapter: EditListAdapter

    private val viewModel: EditListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditListBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.scanResultActivity) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editListAdapter = EditListAdapter()
        binding.rvScanResult.layoutManager = LinearLayoutManager(this@EditListActivity)
        binding.rvScanResult.adapter = editListAdapter

        val scanData = intent.getParcelableExtra<ScanImageResponse>("SCAN_DATA")
        if (scanData != null) {
            viewModel.setScanData(scanData)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.scanData.observe(this) { scanData ->
            if (scanData != null) {
                displayScanData(scanData)
            }
        }
    }

    private fun displayScanData(scanData: ScanImageResponse) {
        Log.d("EditListActivity", "Scan Data: $scanData")
    }
}
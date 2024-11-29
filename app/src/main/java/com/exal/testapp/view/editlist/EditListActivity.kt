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
import com.exal.testapp.databinding.ActivityEditListBinding
import com.exal.testapp.view.adapter.EditListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        ViewCompat.setOnApplyWindowInsetsListener(binding.scanResultActivity) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editListAdapter = EditListAdapter()
        binding.rvScanResult.layoutManager = LinearLayoutManager(this@EditListActivity)
        binding.rvScanResult.adapter = editListAdapter

        fetchData()

    }

    private fun fetchData() {
        val listId = "11"
        lifecycleScope.launch {
            editListViewModel.resultList.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d("EditListActivity", "Loading...")
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val data = resource.data?.firstOrNull()?.items
                        editListAdapter.submitList(data)
                        Log.d("EditListActivity", "Data: $data")
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@EditListActivity, resource.message, Toast.LENGTH_SHORT).show()
                        Log.d("EditListActivity", "Error: ${resource.message}")
                    }
                }
            }
        }
        editListViewModel.fetchResultList(listId)
    }

}
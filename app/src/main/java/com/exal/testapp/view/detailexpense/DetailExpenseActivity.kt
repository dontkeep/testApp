package com.exal.testapp.view.detailexpense

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.databinding.ActivityDetailExpenseBinding
import com.exal.testapp.view.adapter.DetailExpenseAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailExpenseBinding
    private val viewModel: DetailExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvSetup()

        binding.editBtn.setOnClickListener {
            Toast.makeText(this, "Edit List Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.shareBtn.setOnClickListener {
            Toast.makeText(this, "Share List Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun rvSetup() {
        val adapter = DetailExpenseAdapter()

        val layoutManager = LinearLayoutManager(this)
        binding.itemRv.layoutManager = layoutManager
        binding.itemRv.adapter = adapter

//        viewModel.productList.observe(this) {
//            adapter.submitList(it)
//        }
    }
}
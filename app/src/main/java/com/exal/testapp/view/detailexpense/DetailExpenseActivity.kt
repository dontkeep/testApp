package com.exal.testapp.view.detailexpense

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.databinding.ActivityDetailExpenseBinding
import com.exal.testapp.helper.DateFormatter
import com.exal.testapp.helper.formatRupiah
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

        val expenseTitle = intent.getStringExtra(EXTRA_EXPENSE_TITLE)
        val expenseDate = intent.getStringExtra(EXTRA_EXPENSE_DATE)

        binding.dateTv.text = DateFormatter.localizeDate(expenseDate ?: "")

        val expenseId = intent.getIntExtra(EXTRA_EXPENSE_ID, -1)
        if (expenseId != -1) {
            viewModel.getExpenseDetail(expenseId)
        } else {
            Toast.makeText(this, "Invalid expense ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        rvSetup()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.titleTv.text = expenseTitle

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

        viewModel.productList.observe(this) {
            Log.d("DetailExpenseActivity", "Product List: ${it.data?.data?.detailItems}")
            adapter.submitList(it.data?.data?.detailItems)

            var totalPrice = 0
            it.data?.data?.detailItems?.forEach { item ->
                totalPrice += item?.totalPrice?.toDoubleOrNull()?.toInt() ?: 0
            }
            binding.totalPriceTv.text = "Total : " + formatRupiah(totalPrice)
        }
    }

    companion object {
        const val EXTRA_EXPENSE_ID = "extra_expense_id"
        const val EXTRA_EXPENSE_TITLE = "extra_expense_title"
        const val EXTRA_EXPENSE_DATE = "extra_expense_date"
    }
}
package com.exal.testapp.view.editlistdetail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.R
import com.exal.testapp.data.network.response.ProductsItem
import com.exal.testapp.databinding.ActivityEditListDetailBinding
import com.exal.testapp.helper.formatRupiah
import com.exal.testapp.view.adapter.ItemAdapter
import com.exal.testapp.view.createlist.AddManualDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditListDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditListDetailBinding
    val viewModel: EditListDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditListDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val expenseId = intent.getIntExtra(EXTRA_EXPENSE_ID, -1)
        val expenseTitle = intent.getStringExtra(EXTRA_EXPENSE_TITLE)
        Log.d("EditListDetailActivity", "Expense ID: $expenseId, Title: $expenseTitle")

        // TODO (Receive List Intent Data from DetailActivity then send to viewmodel and rv, after that send to server)
        // Existing setup
        val jsonList = intent.getStringExtra(EXTRA_DETAIL_LIST)
        val detailItems: List<ProductsItem> = Gson().fromJson(jsonList, object : TypeToken<List<ProductsItem>>() {}.type)

        // Save to ViewModel
        viewModel.setInitialProductList(detailItems)

        rvSetup()

        binding.titleTv.text = expenseTitle

        binding.fabBottomAppBar.setOnClickListener {
            val dialogFragment = AddManualDialogFragment()
            dialogFragment.show(supportFragmentManager, "addManualDialog")
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.saveBtn.setOnClickListener {
//            val updatedList = viewModel.productList.value ?: emptyList()
//            val updatedJsonList = Gson().toJson(updatedList)
            Toast.makeText(this, "List Updated", Toast.LENGTH_SHORT).show()
        }
        observeViewModel()
    }

    private fun rvSetup() {
        val adapter = ItemAdapter{ item ->
            viewModel.deleteProduct(item)
        }
        binding.itemRv.layoutManager = LinearLayoutManager(this)
        binding.itemRv.adapter = adapter

        // Observe the product list
        viewModel.productList.observe(this) { products ->
            adapter.submitList(products)
        }
    }

    private fun observeViewModel() {
        viewModel.productList.observe(this) { products ->
            (binding.itemRv.adapter as? ItemAdapter)?.submitList(products)
        }

        viewModel.totalPrice.observe(this) { price ->
            binding.totalPriceTv.text = formatRupiah(price)
            Log.d("CreateListActivity", "Total Price: $price")
        }
    }

    companion object {
        val EXTRA_DETAIL_LIST = "extra_detail_list"
        val EXTRA_EXPENSE_ID = "extra_expense_id"
        val EXTRA_EXPENSE_TITLE = "extra_expense_title"
    }
}
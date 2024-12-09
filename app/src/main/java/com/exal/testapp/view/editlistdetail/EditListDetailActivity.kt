package com.exal.testapp.view.editlistdetail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Update
import com.exal.testapp.MainActivity
import com.exal.testapp.R
import com.exal.testapp.data.Resource
import com.exal.testapp.data.network.response.PostListResponse
import com.exal.testapp.data.network.response.ProductsItem
import com.exal.testapp.data.network.response.UpdateListResponse
import com.exal.testapp.data.request.ProductItem
import com.exal.testapp.databinding.ActivityEditListDetailBinding
import com.exal.testapp.helper.formatRupiah
import com.exal.testapp.view.adapter.ItemAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

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

        val jsonList = intent.getStringExtra(EXTRA_DETAIL_LIST)
        val detailItems: List<ProductsItem> = Gson().fromJson(jsonList, object : TypeToken<List<ProductsItem>>() {}.type)
        val type = intent.getStringExtra("list_type")

        viewModel.setInitialProductList(detailItems)

        rvSetup()

        binding.textFieldTitle.editText?.setText(expenseTitle)

        binding.fabBottomAppBar.setOnClickListener {
            val dialogFragment = AddManualDialogFragment()
            dialogFragment.show(supportFragmentManager, "addManualDialog")
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.saveBtn.setOnClickListener {
            Log.d("before save type", "$type")
            handleSaveButtonClick(type, expenseId)
        }
        observeViewModel()
    }

    private fun handleSaveButtonClick(type: String?, id: Int) {
        lifecycleScope.launch {
            val titleRequestBody = createRequestBody(binding.textFieldTitle.editText?.text.toString())
            val typeRequestBody = createRequestBody(type)
            val totalExpensesRequestBody = createRequestBody(viewModel.totalPrice.value.toString())
            val totalItems = viewModel.productList.value?.fold(0) { sum, item ->
                sum + (item.amount ?: 0)
            } ?: 0
            val totalItemsPart = createRequestBody(totalItems.toString())
            val productItemsRequestBody = createRequestBody(
                Gson().toJson(
                    viewModel.productList.value?.map {
                        ProductItem(it.id, it.name, it.amount, it.price, it.detail?.categoryIndex.toString(), it.totalPrice)
                    }
                )
            )

            viewModel.updateData(
                id,
                titleRequestBody,
                productItemsRequestBody,
                typeRequestBody,
                totalExpensesRequestBody,
                totalItemsPart
            ).collect { resource ->
                handleResource(resource)
            }
        }
    }

    private fun createRequestBody(value: String?): okhttp3.RequestBody =
        value.orEmpty().toRequestBody("text/plain".toMediaTypeOrNull())

    private fun handleResource(resource: Resource<UpdateListResponse>) {
        when (resource) {
            is Resource.Loading -> {
                Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
            }
            is Resource.Success -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("TARGET_FRAGMENT", "ExpensesFragment")
                startActivity(intent)
                finish()
            }
            is Resource.Error -> {
                Log.d("EditListDetailActivity", "Error: ${resource.message}")
                Toast.makeText(this, "Error ${resource.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun rvSetup() {
        val adapter = ItemAdapter{ item ->
            viewModel.deleteProduct(item)
        }
        binding.itemRv.layoutManager = LinearLayoutManager(this)
        binding.itemRv.adapter = adapter

        viewModel.productList.observe(this) { products ->
            Log.d("EditListDetailActivity", "Product List: ${products[0].detail?.categoryIndex}")
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
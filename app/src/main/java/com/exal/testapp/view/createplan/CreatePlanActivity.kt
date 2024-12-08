package com.exal.testapp.view.createplan

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
import com.exal.testapp.MainActivity
import com.exal.testapp.R
import com.exal.testapp.data.Resource
import com.exal.testapp.data.network.response.PostListResponse
import com.exal.testapp.data.request.ProductItem
import com.exal.testapp.databinding.ActivityCreatePlanBinding
import com.exal.testapp.helper.DateFormatter
import com.exal.testapp.view.adapter.ItemAdapter
import com.google.gson.Gson
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreatePlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePlanBinding
    val viewModel: CreatePlanViewModel by viewModels()
    private var boughtAtUser = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePlanBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        boughtAtUser = getString(R.string.today)

        rvSetup()

        binding.dateTv.text = boughtAtUser

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.fabBottomAppBar.setOnClickListener {
            val dialogFragment = AddManualDialogFragment()
            dialogFragment.show(supportFragmentManager, "addManualDialog")
        }

        binding.saveButton.setOnClickListener {
            handleSaveButtonClick()
        }

        binding.calendarBtn.setOnClickListener {
            showDatePicker()
        }

        viewModel.productList.observe(this) { products ->
            (binding.itemRv.adapter as? ItemAdapter)?.submitList(products)
        }
    }

    private fun rvSetup() {
        val adapter = ItemAdapter { item ->
            viewModel.deleteProduct(item)
        }
        binding.itemRv.layoutManager = LinearLayoutManager(this)
        binding.itemRv.adapter = adapter
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedDate ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDate
            showTimePicker(calendar)
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun showTimePicker(calendar: Calendar) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText("Select Time")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)

            // Format date and time to "yyyy-MM-dd HH:mm:ss"
            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            boughtAtUser = dateFormat.format(calendar.time)

            // Update UI or show a Toast
            binding.dateTv.text = DateFormatter.normalizeDate(boughtAtUser)
        }

        timePicker.show(supportFragmentManager, "TIME_PICKER")
    }

    private fun handleSaveButtonClick() {
        lifecycleScope.launch {
            val titleRequestBody = createRequestBody(binding.textFieldTitle.editText?.text.toString())
            val typeRequestBody = createRequestBody("Plan")
            val totalExpensesRequestBody = createRequestBody(viewModel.totalPrice.value.toString())
            val totalItems = viewModel.productList.value?.fold(0) { sum, item ->
                sum + (item.amount ?: 0)
            } ?: 0
            val totalItemsPart = createRequestBody(totalItems.toString())
            val productItemsRequestBody = createRequestBody(
                Gson().toJson(
                    viewModel.productList.value?.map {
                        ProductItem(it.name, it.amount, it.price, it.detail?.categoryIndex.toString(), it.totalPrice)
                    }
                )
            )

            val receiptImagePart = null
            val thumbnailImagePart = null

            if (boughtAtUser == getString(R.string.today)){
                val currentDate = System.currentTimeMillis()
                val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                val formattedDate = dateFormat.format(java.util.Date(currentDate))
                boughtAtUser = formattedDate
            }

            val boughtAt = createRequestBody(boughtAtUser)

            viewModel.postData(
                titleRequestBody,
                receiptImagePart,
                thumbnailImagePart,
                productItemsRequestBody,
                typeRequestBody,
                totalExpensesRequestBody,
                totalItemsPart,
                boughtAt
            ).collect { resource ->
                handleResource(resource)
            }
        }
    }

    private fun createRequestBody(value: String?): okhttp3.RequestBody =
        value.orEmpty().toRequestBody("text/plain".toMediaTypeOrNull())

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
                Log.d("CreateListActivity", "Error: ${resource.message}")
                Toast.makeText(this, "Error ${resource.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
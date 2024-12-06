package com.exal.testapp.view.createplan

import android.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import com.exal.testapp.data.network.response.Detail
import com.exal.testapp.data.network.response.ProductsItem
import com.exal.testapp.databinding.AddManualDialogFragmentBinding
import com.exal.testapp.helper.formatRupiah

class AddManualDialogFragment : DialogFragment() {

    private var _binding: AddManualDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val categoryMapping = mapOf(
        0 to "Food",
        1 to "Beauty",
        2 to "Home Living",
        3 to "Drink",
        4 to "Fresh Product",
        5 to "Health",
        6 to "Other"
    )

    private val reverseCategoryMapping = categoryMapping.entries.associate { (key, value) -> value to key }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = params
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddManualDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentProductList = (activity as? CreatePlanActivity)?.viewModel?.productList?.value.orEmpty()
        val highestId = currentProductList.maxOfOrNull { it.id ?: 0 } ?: 0
        val newId = highestId + 1

        val categories = categoryMapping.values.toList()
        val adapter = ArrayAdapter(
            binding.textFieldCategory.context,
            R.layout.simple_list_item_1,
            categories
        )
        (binding.textFieldCategory.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        textListenerAndWatcher()

        updateTotalPrice()

        binding.addButton.setOnClickListener {
            val productName = binding.textFieldName.editText?.text.toString()
            val productPrice = binding.textFieldPrice.editText?.text.toString().toIntOrNull() ?: 0
            val productAmount = binding.textFieldQuantity.editText?.text.toString().toIntOrNull() ?: 0
            val selectedCategory = (binding.textFieldCategory.editText as? AutoCompleteTextView)?.text.toString()
            val categoryKey = reverseCategoryMapping[selectedCategory]

            val newItem = ProductsItem(
                id = newId,
                name = productName,
                price = productPrice,
                amount = productAmount,
                totalPrice = productPrice * productAmount,
                detail = Detail(categoryIndex = categoryKey)
            )

            (activity as? CreatePlanActivity)?.viewModel?.addProduct(newItem)
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun textListenerAndWatcher(){
        binding.textFieldPrice.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTotalPrice()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.textFieldQuantity.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTotalPrice()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateTotalPrice() {
        val price = binding.textFieldPrice.editText?.text.toString().toIntOrNull() ?: 0
        val quantity = binding.textFieldQuantity.editText?.text.toString().toIntOrNull() ?: 0
        val totalPrice = price * quantity
        binding.tvTotal.text = formatRupiah(totalPrice)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
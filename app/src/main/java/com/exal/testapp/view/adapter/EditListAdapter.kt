package com.exal.testapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exal.testapp.data.network.response.ProductsItem
import com.exal.testapp.databinding.ItemEditListBinding
import com.exal.testapp.helper.formatRupiah

class EditListAdapter(private val onItemUpdated: (ProductsItem) -> Unit): ListAdapter<ProductsItem, EditListAdapter.ItemViewHolder>(DIFF_CALLBACK){

    inner class ItemViewHolder(private val binding: ItemEditListBinding) : RecyclerView.ViewHolder(binding.root) {

        private var isUpdating = false

        fun bind(item: ProductsItem) {
            if (binding.textFieldName.editText?.text.toString() != item.name) {
                isUpdating = true
                binding.textFieldName.editText?.setText(item.name)
                isUpdating = false
            }

            if (binding.textFieldCategory.editText?.text.toString() != item.detail?.category) {
                isUpdating = true
                binding.textFieldCategory.editText?.setText(item.detail?.category)
                isUpdating = false
            }

            if (binding.textFieldPrice.editText?.text.toString() != item.price.toString()) {
                isUpdating = true
                binding.textFieldPrice.editText?.setText(item.price.toString())
                isUpdating = false
            }

            if (binding.textFieldQuantity.editText?.text.toString() != item.amount.toString()) {
                isUpdating = true
                binding.textFieldQuantity.editText?.setText(item.amount.toString())
                isUpdating = false
            }

            updateTotal(item.price ?: 0, item.amount ?: 0)

            binding.textFieldName.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !isUpdating) {
                    val updatedName = binding.textFieldName.editText?.text.toString()
                    if (updatedName != item.name) {
                        onItemUpdated(item.copy(name = updatedName))
                    }
                }
            }

            binding.textFieldCategory.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !isUpdating) {
                    val updatedCategory = binding.textFieldCategory.editText?.text.toString()
                    if (updatedCategory != item.detail?.category) {
                        onItemUpdated(item.copy(detail = item.detail?.copy(category = updatedCategory)))
                    }
                }
            }

            binding.textFieldQuantity.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !isUpdating) {
                    val updatedQuantity = binding.textFieldQuantity.editText?.text.toString().toIntOrNull() ?: item.amount
                    val updatedPrice = binding.textFieldPrice.editText?.text.toString().toIntOrNull() ?: item.price
                    if (updatedQuantity != item.amount) {
                        updateTotal(updatedPrice!!, updatedQuantity!!)
                        onItemUpdated(item.copy(amount = updatedQuantity))
                    }
                }
            }

            binding.textFieldPrice.editText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && !isUpdating) {
                    val updatedPrice = binding.textFieldPrice.editText?.text.toString().toIntOrNull() ?: item.price
                    val updatedQuantity = binding.textFieldQuantity.editText?.text.toString().toIntOrNull() ?: item.amount
                    if (updatedPrice != item.price) {
                        updateTotal(updatedPrice!!, updatedQuantity!!)
                        onItemUpdated(item.copy(price = updatedPrice))
                    }
                }
            }
        }

        private fun updateTotal(price: Int, amount: Int) {
            val total = price * amount
            binding.tvTotal.text = formatRupiah(total)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemEditListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProductsItem>() {
            override fun areItemsTheSame(oldItem: ProductsItem, newItem: ProductsItem): Boolean {
                return oldItem.detail?.type == newItem.detail?.type // Bandingkan menggunakan ID (type)
            }

            override fun areContentsTheSame(oldItem: ProductsItem, newItem: ProductsItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}

package com.exal.testapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exal.testapp.data.network.response.ProductsItem
import com.exal.testapp.databinding.ItemDetailListBinding
import com.exal.testapp.helper.formatRupiah

class DetailExpenseAdapter: ListAdapter<ProductsItem, DetailExpenseAdapter.ItemViewHolder>(DIFF_CALLBACK){

    private val categoryMapping = mapOf(
        "0" to "Food",
        "1" to "Beauty",
        "2" to "Home Living",
        "3" to "Drink",
        "4" to "Fresh Product",
        "5" to "Health",
        "6" to "Other"
    )

    inner class ItemViewHolder(private val binding: ItemDetailListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductsItem) {
            with(binding) {
                itemName.text = item.name
                itemCategory.text = categoryMapping[item.detail?.category]
                itemQuantity.text = item.amount.toString()
                itemPrice.text = item.price?.let { formatRupiah(it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemDetailListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProductsItem>(){
            override fun areItemsTheSame(oldItem: ProductsItem, newItem: ProductsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ProductsItem, newItem: ProductsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
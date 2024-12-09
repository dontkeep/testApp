package com.exal.testapp.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exal.testapp.data.network.response.DetailItemsItem
import com.exal.testapp.databinding.ItemEditItemBinding
import com.exal.testapp.helper.formatRupiah

class ItemEditAdapter(private val onEdit: (DetailItemsItem) -> Unit): ListAdapter<DetailItemsItem, ItemEditAdapter.ItemViewHolder>(DIFF_CALLBACK){

    private val categoryMapping = mapOf(
        "0" to "Food",
        "1" to "Beauty",
        "2" to "Home Living",
        "3" to "Drink",
        "4" to "Fresh Product",
        "5" to "Health",
        "6" to "Other"
    )

    inner class ItemViewHolder(private val binding: ItemEditItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DetailItemsItem) {
            val category = item.category
            Log.d("ItemEditAdapter", "Category: $category")
            with(binding) {
                itemName.text = item.name
                itemCategory.text = categoryMapping[category.toString()]
                itemQuantity.text = item.amount.toString()
                itemPrice.text = item.price?.let { formatRupiah(it.toDouble().toInt()) }
                editBtn.setOnClickListener {
                    onEdit(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemEditItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DetailItemsItem>(){
            override fun areItemsTheSame(oldItem: DetailItemsItem, newItem: DetailItemsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DetailItemsItem, newItem: DetailItemsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
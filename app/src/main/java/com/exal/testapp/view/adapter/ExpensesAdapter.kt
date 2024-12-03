package com.exal.testapp.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.exal.testapp.R
import com.exal.testapp.data.network.response.DataItem
import com.exal.testapp.data.network.response.ListsItem
import com.exal.testapp.databinding.ItemRowExpenseBinding
import com.exal.testapp.helper.DateFormatter
import com.exal.testapp.helper.formatRupiah

class ExpensesAdapter: ListAdapter<ListsItem, ExpensesAdapter.ItemViewHolder>(DIFF_CALLBACK){

    inner class ItemViewHolder(private val binding: ItemRowExpenseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListsItem) {
            binding.itemImage.setImageResource(R.drawable.placeholder)
            val totalExpensesInt = item.totalExpenses?.toDoubleOrNull()?.toInt()
            binding.itemPrice.text = totalExpensesInt?.let { formatRupiah(it) } ?: "0"
            "${item.totalItems} Items".also { binding.itemTotal.text = it }
            binding.itemDate.text = "12-1-2024"

            Log.d("ExpensesAdapter", "Item: $item.image")
            Glide.with(itemView.context)
                .load(item.image)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.placeholder)
                        .error(R.drawable.ic_close)
                )
                .into(binding.itemImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemRowExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListsItem>(){
            override fun areItemsTheSame(oldItem: ListsItem, newItem: ListsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListsItem, newItem: ListsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}

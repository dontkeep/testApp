package com.exal.testapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.exal.testapp.R
import com.exal.testapp.data.local.entity.ListEntity
import com.exal.testapp.databinding.ItemRowExpenseBinding
import com.exal.testapp.helper.DateFormatter
import com.exal.testapp.helper.formatRupiah

class ExpensesAdapter(private val onItemClick: (Int, String, String) -> Unit): PagingDataAdapter<ListEntity, ExpensesAdapter.ItemViewHolder>(DIFF_CALLBACK){

    inner class ItemViewHolder(private val binding: ItemRowExpenseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListEntity) {
            binding.itemImage.setImageResource(R.drawable.placeholder)
            val totalExpensesInt = item.totalExpenses?.toDoubleOrNull()?.toInt()
            binding.itemPrice.text = totalExpensesInt?.let { formatRupiah(it) } ?: "0"
            "${item.totalItems} Items".also { binding.itemTotal.text = it }
            binding.itemDate.text = DateFormatter.localizeDate(item.boughtAt ?: "")
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
        if (item != null) {
            holder.bind(item)
        }
        holder.itemView.setOnClickListener {
            if (item != null) {
                onItemClick(item.id.toInt(), item.title ?: "", item.boughtAt ?: "")
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEntity>(){
            override fun areItemsTheSame(oldItem: ListEntity, newItem: ListEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListEntity, newItem: ListEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}

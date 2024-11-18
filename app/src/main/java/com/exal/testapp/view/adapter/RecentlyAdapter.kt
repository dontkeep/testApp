package com.exal.testapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.exal.testapp.databinding.ItemRowExpenseBinding
import com.exal.testapp.helper.formatRupiah
import com.exal.testapp.view.home.Item

class RecentlyAdapter(private val itemList: List<Item>) : RecyclerView.Adapter<RecentlyAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val binding: ItemRowExpenseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.itemImage.setImageResource(item.itemImage)
            binding.itemPrice.text = formatRupiah(item.itemPrice)
            binding.itemTotal.text = "${item.itemTotal} Items"
            binding.itemDate.text = item.itemDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemRowExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size
}

package com.exal.testapp.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exal.testapp.data.network.response.ItemsItem
import com.exal.testapp.databinding.ItemEditListBinding
import com.exal.testapp.helper.formatRupiah

class EditListAdapter: ListAdapter<ItemsItem, EditListAdapter.ItemViewHolder>(DIFF_CALLBACK){

    inner class ItemViewHolder(private val binding: ItemEditListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemsItem) {
            binding.textFieldName.editText?.setText(item.itemName)
            binding.textFieldPrice.editText?.setText(item.itemPrice.toString())
            binding.textFieldQuantity.editText?.setText(item.itemQuantity.toString())
            binding.textFieldCategory.editText?.setText(item.itemCategory)

            val total = item.itemPrice * item.itemQuantity
            binding.tvTotal.text = formatRupiah(total)
            Log.d("EditListAdapter", "Item: $item")
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
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemsItem>(){
            override fun areItemsTheSame(oldItem: ItemsItem, newItem: ItemsItem): Boolean {
                return oldItem.listId == newItem.listId
            }

            override fun areContentsTheSame(oldItem: ItemsItem, newItem: ItemsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}

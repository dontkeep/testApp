package com.exal.testapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exal.testapp.data.network.response.ListsItem
import com.exal.testapp.databinding.ItemPlanListBinding

class PlanAdapter(private val onItemClick: (Int, String) -> Unit): ListAdapter<ListsItem, PlanAdapter.ItemViewHolder>(DIFF_CALLBACK){

    inner class ItemViewHolder(private val binding: ItemPlanListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListsItem) {
            binding.titleTv.text = item.title
            "${item.totalItems} Items".also { binding.totalTv.text = it }
            binding.dayTv.text = item.createdAt?.substring(8, 10)
            binding.dateTv.text = item.createdAt?.substring(5, 7)
            binding.yearTv.text = item.createdAt?.substring(0, 4)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemPlanListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClick(item.id ?: -1, item.title ?: "")
        }
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

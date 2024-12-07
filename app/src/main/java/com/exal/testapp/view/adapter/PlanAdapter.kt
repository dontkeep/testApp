package com.exal.testapp.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exal.testapp.data.local.entity.ListEntity
import com.exal.testapp.data.network.response.ListsItem
import com.exal.testapp.databinding.ItemPlanListBinding
import com.exal.testapp.helper.DateFormatter

class PlanAdapter(private val onItemClick: (Int, String) -> Unit): PagingDataAdapter<ListEntity, PlanAdapter.ItemViewHolder>(DIFF_CALLBACK){

    inner class ItemViewHolder(private val binding: ItemPlanListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListEntity) {
            binding.titleTv.text = item.title
            "${item.totalItems} Items".also { binding.totalTv.text = it }
            binding.dayTv.text = DateFormatter.localizeDay(item.boughtAt ?: "")
            binding.dateTv.text = DateFormatter.localizeMonth(item.boughtAt ?: "")
            binding.yearTv.text = DateFormatter.localizeYear(item.boughtAt ?: "")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemPlanListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
        holder.itemView.setOnClickListener {
            if (item != null) {
                onItemClick(item.id.toInt(), item.title ?: "")
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

package com.exal.testapp

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exal.testapp.databinding.ItemOneBinding

class ItemAdapter: ListAdapter<ListItem, ItemAdapter.ViewHolder>(DIFF_CALLBACK) {
    class ViewHolder(private val binding: ItemOneBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(eventItem: ListItem) {
            binding.apply {
                val title = eventItem.name?.split(" ")?.take(10)?.joinToString(" ")
                itemTitle.text = buildString {
                    append(title)
                    append("...")
                }
                summaryVertical.text = eventItem.summary
                summaryVertical.ellipsize = TextUtils.TruncateAt.END
                summaryVertical.maxLines = 1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemOneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(
                oldItem: ListItem,
                newItem: ListItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListItem,
                newItem: ListItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
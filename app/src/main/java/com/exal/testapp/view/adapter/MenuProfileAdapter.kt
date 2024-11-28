package com.exal.testapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.exal.testapp.R

class MenuProfileAdapter(
    private val context: Context,
    private val menuItems: List<MenuItem>
) : BaseAdapter() {

    override fun getCount(): Int = menuItems.size

    override fun getItem(position: Int): Any = menuItems[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_menu_profile, parent, false)

        val icon = view.findViewById<ImageView>(R.id.itemIcon)
        val text = view.findViewById<TextView>(R.id.itemText)

        val menuItem = menuItems[position]
        icon.setImageResource(menuItem.iconResId)
        text.text = menuItem.title

        return view
    }
}

data class MenuItem(val title: String, val iconResId: Int)

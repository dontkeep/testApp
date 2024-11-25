package com.exal.testapp.view.home

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.R
import com.exal.testapp.databinding.FragmentHomeBinding
import com.exal.testapp.helper.formatRupiah
import com.exal.testapp.view.createlist.CreateListActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private var totalPrice = 0
    private var totalItem = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.total.text = formatRupiah(totalPrice)
        binding.items.text = "$totalItem Items"

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), CreateListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        // Load data from string-array resources
        val itemPrices = resources.getStringArray(R.array.item_prices).map { it.toInt() }
        val itemTotals = resources.getStringArray(R.array.item_totals).map { it.toInt() }
        val itemDates = resources.getStringArray(R.array.item_dates)
        val itemImages = resources.obtainTypedArray(R.array.item_images)

        // Prepare the data for RecyclerView
        val itemList = mutableListOf<Item>()
        for (i in 0 until 5) { // Ambil 5 data pertama
            // Ambil data untuk item
            val price = itemPrices[i]
            val total = itemTotals[i]
            val imageRes = itemImages.getResourceId(i, -1)

            // Hitung total price dan total items
            totalPrice += price
            totalItem += total

            itemList.add(
                Item(
                    itemImage = imageRes,
                    itemPrice = price,
                    itemTotal = total,
                    itemDate = itemDates[i]
                )
            )
        }
        itemImages.recycle()

        // Set up RecyclerView adapter
//        val adapter = RecentlyAdapter(itemList)
        binding.itemRv.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class Item(
    val itemImage: Int,
    val itemPrice: Int,
    val itemTotal: Int,
    val itemDate: String
)

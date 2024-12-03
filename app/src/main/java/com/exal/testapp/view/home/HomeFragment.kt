package com.exal.testapp.view.home

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.data.Resource
import com.exal.testapp.databinding.FragmentHomeBinding
import com.exal.testapp.helper.formatRupiah
import com.exal.testapp.view.adapter.ExpensesAdapter
import com.exal.testapp.view.createlist.CreateListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()

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

        val adapter = ExpensesAdapter()
        binding.rvExpense.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpense.adapter = adapter

        homeViewModel.getExpenseList()

        homeViewModel.expenses.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    resource.data?.data?.lists?.take(5).let { expenseList ->
                        adapter.submitList(expenseList)
                    }
                    resource.data?.data?.lists?.forEach { expense ->
                        totalPrice += expense?.totalExpenses?.toDouble()?.toInt() ?: 0
                        binding.total.text = formatRupiah(totalPrice)
                        totalItem += expense?.totalItems ?: 0
                        binding.items.text = "$totalItem Items"
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    resource.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        binding.total.text = formatRupiah(totalPrice)
        binding.items.text = "$totalItem Items"

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), CreateListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

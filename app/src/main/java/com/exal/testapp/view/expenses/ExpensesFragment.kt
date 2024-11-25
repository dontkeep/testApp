package com.exal.testapp.view.expenses

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.data.Resource
import com.exal.testapp.data.network.response.DataItem
import com.exal.testapp.databinding.FragmentExpensesBinding
import com.exal.testapp.helper.MonthYearPickerDialog
import com.exal.testapp.view.adapter.ExpensesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols

@AndroidEntryPoint
class ExpensesFragment : Fragment() {
    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val expensesViewModel: ExpensesViewModel by viewModels()

        val adapter = ExpensesAdapter()
        binding.rvExpense.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpense.adapter = adapter

        expensesViewModel.getExpenses("1").observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Show loading indicator (e.g., ProgressBar)
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    // Hide loading indicator
                    binding.progressBar.visibility = View.GONE
                    Log.d("ExpensesFragment", "Data: ${resource.data}")
                    // Update adapter with data
                    val dataList = resource.data?.firstOrNull()?.data
                    adapter.submitList(dataList)
                }

                is Resource.Error -> {
                    // Hide loading indicator
                    binding.progressBar.visibility = View.GONE
                    // Show error message (e.g., Toast or Snackbar)
                    Log.d("ExpensesFragment", "Error: ${resource.message}")
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.icCalender.setOnClickListener {
            MonthYearPickerDialog(requireContext()) { month, year ->
                val monthName = DateFormatSymbols().months[month]
                Toast.makeText(requireContext(), "$monthName $year", Toast.LENGTH_SHORT).show()
            }.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
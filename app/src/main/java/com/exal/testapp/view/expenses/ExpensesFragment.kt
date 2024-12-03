package com.exal.testapp.view.expenses

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
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

    val expenseViewModel: ExpensesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ExpensesAdapter()
        binding.rvExpense.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpense.adapter = adapter

        expenseViewModel.expenses.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    resource.data?.data?.lists?.let { expenseList ->
                        adapter.submitList(expenseList)
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

        expenseViewModel.getExpenseList()

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
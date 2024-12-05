package com.exal.testapp.view.expenses

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.databinding.FragmentExpensesBinding
import com.exal.testapp.helper.MonthYearPickerDialog
import com.exal.testapp.view.adapter.ExpensesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols

@AndroidEntryPoint
class ExpensesFragment : Fragment() {
    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    private val expenseViewModel: ExpensesViewModel by viewModels()
    private lateinit var pagingAdapter: ExpensesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagingAdapter = ExpensesAdapter()
        binding.rvExpense.layoutManager = LinearLayoutManager(context)
        binding.rvExpense.adapter = pagingAdapter

        viewLifecycleOwner.lifecycleScope.launch  {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                expenseViewModel.getLists("Track").collect { pagingData ->
                    pagingAdapter.submitData(pagingData)
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
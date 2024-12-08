package com.exal.testapp.view.plan

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.R
import com.exal.testapp.data.Resource
import com.exal.testapp.databinding.FragmentExpensesBinding
import com.exal.testapp.databinding.FragmentPlanBinding
import com.exal.testapp.helper.MonthYearPickerDialog
import com.exal.testapp.helper.formatRupiah
import com.exal.testapp.view.adapter.ExpensesAdapter
import com.exal.testapp.view.adapter.LoadingStateAdapter
import com.exal.testapp.view.adapter.PlanAdapter
import com.exal.testapp.view.createplan.CreatePlanActivity
import com.exal.testapp.view.detailexpense.DetailExpenseActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

@AndroidEntryPoint
class PlanFragment : Fragment() {
    private var _binding: FragmentPlanBinding? = null
    private val binding get() = _binding!!
    private val planViewModel: PlanViewModel by viewModels()
    private lateinit var pagingAdapter: PlanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagingAdapter = PlanAdapter{ id, title, date ->
            navigateToDetail(id = id, title = title, date = date)
        }

        val loadingStateAdapter = LoadingStateAdapter { pagingAdapter.retry() }
        binding.rvPlan.adapter = pagingAdapter.withLoadStateFooter(
            footer = loadingStateAdapter
        )

        binding.rvPlan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlan.adapter = pagingAdapter

        lifecycleScope.launch {
            planViewModel.getLists("Plan", null, null)
            planViewModel.expenses.observe(viewLifecycleOwner) { pagingData ->
                pagingAdapter.submitData(lifecycle, pagingData)
            }
        }

        binding.icCalender.setOnClickListener {
            MonthYearPickerDialog(requireContext()) { month, year ->
                lifecycleScope.launch {
                    planViewModel.getLists("Plan", month, year)
                    planViewModel.expenses.observe(viewLifecycleOwner) { pagingData ->
                        pagingAdapter.submitData(lifecycle, pagingData)
                    }
                }
            }.show()
        }

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), CreatePlanActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToDetail(id: Int, title: String, date: String) {
        val intent = Intent(requireContext(), DetailExpenseActivity::class.java)
        intent.putExtra(DetailExpenseActivity.EXTRA_EXPENSE_ID, id)
        intent.putExtra(DetailExpenseActivity.EXTRA_EXPENSE_TITLE, title)
        intent.putExtra(DetailExpenseActivity.EXTRA_EXPENSE_DATE, date)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.exal.testapp.view.plan

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.exal.testapp.R
import com.exal.testapp.data.Resource
import com.exal.testapp.databinding.FragmentExpensesBinding
import com.exal.testapp.databinding.FragmentPlanBinding
import com.exal.testapp.helper.MonthYearPickerDialog
import com.exal.testapp.helper.formatRupiah
import com.exal.testapp.view.adapter.PlanAdapter
import com.exal.testapp.view.createplan.CreatePlanActivity
import com.exal.testapp.view.detailexpense.DetailExpenseActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

@AndroidEntryPoint
class PlanFragment : Fragment() {
    private var _binding: FragmentPlanBinding? = null
    private val binding get() = _binding!!
    private val planViewModel: PlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val format = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))

        val adapter = PlanAdapter{ id, title ->
            navigateToDetail(id = id, title = title)
        }

        binding.rvPlan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlan.adapter = adapter

        planViewModel.getExpenseList()

        planViewModel.expenses.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    resource.data?.data?.lists.let { expenseList ->
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

        binding.icCalender.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.show(parentFragmentManager, "DatePicker")

            datePicker.addOnPositiveButtonClickListener {
                Toast.makeText(requireContext(), format.format(it), Toast.LENGTH_SHORT).show()
            }
        }

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), CreatePlanActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToDetail(id: Int, title: String) {
        val intent = Intent(requireContext(), DetailExpenseActivity::class.java)
        intent.putExtra(DetailExpenseActivity.EXTRA_EXPENSE_ID, id)
        intent.putExtra(DetailExpenseActivity.EXTRA_EXPENSE_TITLE, title)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
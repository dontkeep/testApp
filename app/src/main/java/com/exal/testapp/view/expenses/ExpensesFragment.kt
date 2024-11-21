package com.exal.testapp.view.expenses

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.exal.testapp.R
import com.exal.testapp.databinding.FragmentExpensesBinding
import com.exal.testapp.databinding.FragmentHomeBinding
import com.exal.testapp.databinding.FragmentProfileBinding
import com.exal.testapp.helper.MonthYearPickerDialog
import java.text.DateFormatSymbols

class ExpensesFragment : Fragment() {
    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpensesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
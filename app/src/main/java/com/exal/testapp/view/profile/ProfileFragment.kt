package com.exal.testapp.view.profile

import android.os.Bundle
import android.view.View
import androidx.compose.material3.MaterialTheme
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.exal.testapp.LineSample
import com.exal.testapp.R
import com.exal.testapp.databinding.FragmentProfileBinding
import com.exal.testapp.helper.MonthYearPickerDialog
import java.text.DateFormatSymbols

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        // Set content for ComposeView
        binding.composeView.setContent {
            MaterialTheme {
                LineSample()
            }
        }

        // Setup for calendar button
        binding.calendarButton.setOnClickListener {
            MonthYearPickerDialog(requireContext()) { month, year ->
                val monthName = DateFormatSymbols().months[month]
                binding.dateTxt.text = "$monthName $year"
            }.show()
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

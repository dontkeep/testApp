package com.exal.testapp.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.exal.testapp.LineSample
import com.exal.testapp.databinding.FragmentProfileBinding
import com.exal.testapp.helper.MonthYearPickerDialog
import com.exal.testapp.view.appsettings.AppSettingsActivity
import java.text.DateFormatSymbols

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

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

        binding.accSeting.setOnClickListener {
            val intent = Intent(requireContext(), AppSettingsActivity::class.java)
            startActivity(intent)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

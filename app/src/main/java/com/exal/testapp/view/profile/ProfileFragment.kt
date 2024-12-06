package com.exal.testapp.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.view.menu.MenuAdapter
import androidx.compose.material3.MaterialTheme
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.exal.testapp.LineSample
import com.exal.testapp.R
import com.exal.testapp.data.Resource
import com.exal.testapp.databinding.FragmentProfileBinding
import com.exal.testapp.helper.MonthYearPickerDialog
import com.exal.testapp.helper.manager.TokenManager
import com.exal.testapp.view.adapter.MenuItem
import com.exal.testapp.view.adapter.MenuProfileAdapter
import com.exal.testapp.view.appsettings.AppSettingsActivity
import com.exal.testapp.view.landing.LandingActivity
import com.exal.testapp.view.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormatSymbols
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var tokenManager: TokenManager

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

        val menuItems = listOf(
            MenuItem("Account Settings", R.drawable.ic_account_settings),
            MenuItem("App Setting", R.drawable.ic_setting),
            MenuItem("Logout", R.drawable.ic_logout)
        )

        val adapter = MenuProfileAdapter(requireContext(), menuItems)
        binding.listViewMenu.adapter = adapter

        binding.listViewMenu.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> Toast.makeText(requireContext(), "Account Settings clicked", Toast.LENGTH_SHORT).show()
                1 -> {
                    val intent = Intent(requireContext(), AppSettingsActivity::class.java)
                    startActivity(intent)
                }
                2 -> {
                    viewModel.logout()
                    tokenManager.clearToken()
                    viewModel.clearDatabase()
                    logoutObserver()
                }
            }
        }

        binding.composeView.setContent {
            MaterialTheme {
                LineSample()
            }
        }

        binding.calendarButton.setOnClickListener {
            MonthYearPickerDialog(requireContext()) { month, year ->
                val monthName = DateFormatSymbols().months[month]
                binding.dateTxt.text = "$monthName $year"
            }.show()
        }

//        binding.accSetting.setOnClickListener {
//            val intent = Intent(requireContext(), AppSettingsActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun logoutObserver() {
        viewModel.logoutState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val intent = Intent(requireContext(), LandingActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

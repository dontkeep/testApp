package com.exal.testapp.view.appsettings

import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.exal.testapp.R
import com.exal.testapp.databinding.ActivityAppSettingsBinding
import com.exal.testapp.databinding.BottomSheetLanguageBinding
import com.exal.testapp.databinding.BottomSheetThemeBinding
import com.exal.testapp.view.adapter.MenuAppSettingAdapter
import com.exal.testapp.view.adapter.MenuItemApp
import com.google.android.material.bottomsheet.BottomSheetDialog

class AppSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val menuItemsApp = listOf(
            MenuItemApp("Theme", R.drawable.ic_theme),
            MenuItemApp("Language", R.drawable.ic_language),
        )

        val adapter = MenuAppSettingAdapter(this, menuItemsApp)
        binding.listViewMenu.adapter = adapter

        binding.listViewMenu.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> showThemeBottomSheet()
                1 -> showLanguageBottomSheet()
            }
        }

    }

    private fun showThemeBottomSheet() {
        // Inflate BottomSheet layout using View Binding
        val bottomSheetBinding = BottomSheetThemeBinding.inflate(layoutInflater)

        // Initialize BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        // Set current theme (example: light mode by default)
//        val currentTheme = getCurrentTheme()
//        if (currentTheme == "light") {
//            bottomSheetBinding.radioLightMode.isChecked = true
//        } else {
//            bottomSheetBinding.radioDarkMode.isChecked = true
//        }

        // Save button click listener
        bottomSheetBinding.btnSave.setOnClickListener {
//            val selectedTheme = when (bottomSheetBinding.radioGroupTheme.checkedRadioButtonId) {
//                R.id.radioLightMode -> "light"
//                R.id.radioDarkMode -> "dark"
//                else -> "light"
//            }
//            setThemePreference(selectedTheme)
//            bottomSheetDialog.dismiss()
//
//            // Apply theme change immediately
//            applyTheme(selectedTheme)
        }

        // Show BottomSheet
        bottomSheetDialog.show()
    }

    private fun showLanguageBottomSheet() {
        // Inflate layout BottomSheet
        val bottomSheetBinding = BottomSheetLanguageBinding.inflate(layoutInflater)

        // Initialize BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        // Get current language preference
//        val currentLanguage = getCurrentLanguage()
//        if (currentLanguage == "id") {
//            bottomSheetBinding.radioIndonesian.isChecked = true
//        } else {
//            bottomSheetBinding.radioEnglish.isChecked = true
//        }

        // Save button click listener
//        bottomSheetBinding.btnSaveLanguage.setOnClickListener {
//            val selectedLanguage = when (bottomSheetBinding.radioGroupLanguage.checkedRadioButtonId) {
//                R.id.radioIndonesian -> "id"
//                R.id.radioEnglish -> "en"
//                else -> "en"
//            }
//            setLanguagePreference(selectedLanguage)
//            bottomSheetDialog.dismiss()
//
//            // Apply language change (restart activity to apply localization)
//            applyLanguage(selectedLanguage)
//        }

        // Show BottomSheet
        bottomSheetDialog.show()
    }



}
package com.exal.testapp.view.appsettings

import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.drawable.DrawableCompat.applyTheme
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.exal.testapp.R
import com.exal.testapp.databinding.ActivityAppSettingsBinding
import com.exal.testapp.databinding.BottomSheetLanguageBinding
import com.exal.testapp.databinding.BottomSheetThemeBinding
import com.exal.testapp.helper.manager.ThemeManager
import com.exal.testapp.view.adapter.MenuAppSettingAdapter
import com.exal.testapp.view.adapter.MenuItemApp
import com.google.android.material.bottomsheet.BottomSheetDialog

class AppSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppSettingsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val menuItemsApp = listOf(
            MenuItemApp("Theme", R.drawable.ic_theme),
            MenuItemApp("Language", R.drawable.ic_language),
            MenuItemApp("About App", R.drawable.ic_info)
        )

        val adapter = MenuAppSettingAdapter(this, menuItemsApp)
        binding.listViewMenu.adapter = adapter

        binding.listViewMenu.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> showThemeBottomSheet()
                1 -> showLanguageBottomSheet()
                2 -> Toast.makeText(this, "About App", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showThemeBottomSheet() {
        val bottomSheetBinding = BottomSheetThemeBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        // Mendapatkan status Dark Mode dari ThemeManager
        val themeManager = ThemeManager(getSharedPreferences("app_prefs", MODE_PRIVATE))
        val isDarkModeEnabled = themeManager.isDarkModeEnabled()

        // Menyesuaikan RadioButton dengan status Dark Mode
        if (isDarkModeEnabled) {
            bottomSheetBinding.radioDarkMode.isChecked = true
        } else {
            bottomSheetBinding.radioLightMode.isChecked = true
        }

        // Simpan pilihan tema
        bottomSheetBinding.btnSave.setOnClickListener {
            val isDarkModeSelected = bottomSheetBinding.radioDarkMode.isChecked
            themeManager.saveDarkModeEnabled(isDarkModeSelected)
            bottomSheetDialog.dismiss()

            // Terapkan tema baru
            applyTheme(isDarkModeSelected)
        }

        bottomSheetDialog.show()
    }

    private fun applyTheme(isDarkMode: Boolean) {
        if (isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
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
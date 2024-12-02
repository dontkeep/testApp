package com.exal.testapp.helper.manager

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager(private val sharedPreferences: SharedPreferences) {
    companion object {
        private const val DARK_MODE_KEY = "dark_mode"
    }

    fun saveDarkModeEnabled(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_MODE_KEY, isEnabled).apply()
    }

    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(DARK_MODE_KEY, false) // Default ke false (Light Mode)
    }
}


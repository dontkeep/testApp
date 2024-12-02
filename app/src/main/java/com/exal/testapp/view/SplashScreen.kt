package com.exal.testapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.exal.testapp.DebugMenu
import com.exal.testapp.R
import com.exal.testapp.helper.manager.ThemeManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        lifecycleScope.launch {
            val themeManager = ThemeManager(getSharedPreferences("app_prefs", MODE_PRIVATE))
            val isDarkModeEnabled = themeManager.isDarkModeEnabled()

            if (isDarkModeEnabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            delay(1.seconds)
            startActivity(Intent(this@SplashScreen, DebugMenu::class.java))
            finish()
        }
    }
}
package com.exal.testapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.exal.testapp.databinding.ActivityDebugMenuBinding
import com.exal.testapp.helper.manager.ThemeManager
import com.exal.testapp.view.intro.IntroActivity
import com.exal.testapp.view.perspective.PerspectiveActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DebugMenu : AppCompatActivity() {

    private lateinit var binding: ActivityDebugMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDebugMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.introBtn.setOnClickListener {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }

        binding.homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.OpenCVBtn.setOnClickListener {
            val intent = Intent(this, PerspectiveActivity::class.java)
            startActivity(intent)
        }

    }
}
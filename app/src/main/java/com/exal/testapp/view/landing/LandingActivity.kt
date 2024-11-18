package com.exal.testapp.view.landing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.exal.testapp.databinding.ActivityLandingBinding
import com.exal.testapp.view.login.LoginActivity
import com.exal.testapp.view.register.RegisterActivity

class LandingActivity : AppCompatActivity() {

    private var binding: ActivityLandingBinding? = null
    private val _binding get() = binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        _binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
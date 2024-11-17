package com.exal.testapp.view.landing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.exal.testapp.databinding.ActivityLandingBinding

class LandingActivity : AppCompatActivity() {

    private var binding: ActivityLandingBinding? = null
    private val _binding get() = binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }
}
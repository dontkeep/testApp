package com.exal.testapp.view.login

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.exal.testapp.MainActivity
import com.exal.testapp.data.Resource
import com.exal.testapp.databinding.ActivityLoginBinding
import com.exal.testapp.view.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.noAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        loginViewModel.loginState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.loginButton.isEnabled = false
                    binding.textFieldEmail.isEnabled = false
                    binding.textFieldPassword.isEnabled = false
                    binding.root.foreground = ColorDrawable(Color.parseColor("#80000000"))
                }
                is Resource.Success -> {
                    resetUIState()
                    binding.progressBar.visibility = View.GONE
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    resetUIState()
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun resetUIState() {
        binding.progressBar.visibility = View.GONE
        binding.loginButton.isEnabled = true
        binding.textFieldEmail.isEnabled = true
        binding.textFieldPassword.isEnabled = true
        binding.root.foreground = null
    }

//    Uncomment this function if you want to generate a unique device ID
//    fun getOrCreateDeviceId(context: Context): String {
//        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        val deviceIdKey = "device_id"
//
//        var deviceId = sharedPreferences.getString(deviceIdKey, null)
//        if (deviceId == null) {
//            deviceId = UUID.randomUUID().toString()
//            sharedPreferences.edit().putString(deviceIdKey, deviceId).apply()
//        }
//        return deviceId
//    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            val username = binding.textFieldEmail.editText?.text.toString()
            val password = binding.textFieldPassword.editText?.text.toString()
//            val deviceId = getOrCreateDeviceId(this) // Generate or retrieve the device ID and use it on the api if needed
            if (username.isNotBlank() && password.isNotBlank()) {
                loginViewModel.login(username, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
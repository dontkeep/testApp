package com.exal.testapp.view.register

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.exal.testapp.MainActivity
import com.exal.testapp.data.Resource
import com.exal.testapp.databinding.ActivityRegisterBinding
import com.exal.testapp.view.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListener()

        binding.privacyPolicy.setOnClickListener {
            //TODO: Implement go to privacy policy
        }
    }

    private fun setupListener(){
        binding.loginButton.setOnClickListener {
            val username = binding.textFieldEmail.editText?.text.toString()
            val email = binding.textFieldEmail.editText?.text.toString()
            val password = binding.textFieldPassword.editText?.text.toString()
            val confirmPassword = binding.textFieldConfirmPassword.editText?.text.toString()

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (username.isNotBlank() && password.isNotBlank()) {
                registerViewModel.register(username, email, password, confirmPassword)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers(){
        registerViewModel.registerState.observe(this) { resource ->
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
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
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
}
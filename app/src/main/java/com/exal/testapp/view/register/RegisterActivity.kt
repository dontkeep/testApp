package com.exal.testapp.view.register

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.exal.testapp.R
import com.exal.testapp.data.Resource
import com.exal.testapp.databinding.ActivityRegisterBinding
import com.exal.testapp.view.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


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

    private fun createRequestBody(value: String?): okhttp3.RequestBody =
        value.orEmpty().toRequestBody("text/plain".toMediaTypeOrNull())

    private fun setupListener() {
        binding.loginButton.setOnClickListener {
            val usernameText = binding.textFieldUsername.editText?.text.toString()
            val emailText = binding.textFieldEmail.editText?.text.toString()
            val passwordText = binding.textFieldPassword.editText?.text.toString()
            val confirmPasswordText = binding.textFieldConfirmPassword.editText?.text.toString()

            if (usernameText.isNotBlank() && emailText.isNotBlank() && passwordText.isNotBlank()) {
                if (passwordText != confirmPasswordText) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val username = createRequestBody(usernameText)
                val email = createRequestBody(emailText)
                val password = createRequestBody(passwordText)
                val confirmPassword = createRequestBody(confirmPasswordText)

                val drawable = AppCompatResources.getDrawable(this, R.drawable.avatar)
                val file = File.createTempFile("default_profile", ".jpg", cacheDir)
                file.outputStream().use {
                    val bitmap = (drawable as BitmapDrawable).bitmap
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }

                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val profileImage =
                    MultipartBody.Part.createFormData("profile_image", file.name, requestBody)

                registerViewModel.register(username, email, password, confirmPassword, profileImage)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
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
                    Log.d("RegisterActivity", "Error: ${resource.message}")
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
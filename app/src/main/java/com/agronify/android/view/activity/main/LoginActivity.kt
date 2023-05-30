package com.agronify.android.view.activity.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.viewModels
import com.agronify.android.R
import com.agronify.android.databinding.ActivityLoginBinding
import com.agronify.android.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupAction()
    }

    private fun setupView() {
        hideStatusBar()
        setupAppBar()
        setupFeature()
    }

    private fun setupAction() {
        val valid = validateLogin()
        var isNavigatedToMain = false
        binding.apply {
            btnLogin.setOnClickListener {
                loginViewModel.apply {
                    isLoading.observe(this@LoginActivity) {
                        if (!it && error.value.isNullOrEmpty()) {
                            cpiLogin.visibility = View.GONE
                            if (!isNavigatedToMain) {
                                isNavigatedToMain = true
                                navigateToMain()
                            }
                        } else if(!error.value.isNullOrEmpty()) {
                            cpiLogin.visibility = View.GONE
                            showToast(error.value!!)
                        } else {
                            cpiLogin.visibility = View.VISIBLE
                        }
                    }

                    if (valid && !edLoginEmail.text.isNullOrEmpty() && !edLoginPassword.text.isNullOrEmpty()) {
                        userLogin(
                            edLoginEmail.text.toString(),
                            edLoginPassword.text.toString()
                        )
                    } else {
                        showToast(getString(R.string.error_form))
                    }
                }
            }
        }
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.apply {
                statusBarColor = getColor(R.color.agronify_white)
                insetsController?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            }
        } else {
            window.apply {
                statusBarColor = resources.getColor(R.color.agronify_white)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }

    private fun setupAppBar() {
        binding.topAppBar.apply {
            setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun setupFeature() {
        binding.apply {
            tvLoginForgotPassword.setOnClickListener {}

            tvRegister.setOnClickListener {
                Intent(this@LoginActivity, RegisterActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun navigateToMain() {
        setUser()
        Intent(this@LoginActivity, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
            finish()
        }
    }

    private fun setUser() {
        loginViewModel.setNewUser(false)
    }

    private fun validateLogin(): Boolean {
        binding.edLoginEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.tilLoginEmail.error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilLoginEmail.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                binding.tilLoginEmail.error = if (s.isNullOrEmpty()) {
                    binding.btnLogin.isEnabled = false
                    getString(R.string.error_empty_email)
                } else if (!Patterns.EMAIL_ADDRESS.matcher(s).matches() && s.isNotEmpty()) {
                    binding.btnLogin.isEnabled = false
                    getString(R.string.error_invalid_email)
                } else {
                    binding.btnLogin.isEnabled = true
                    null
                }
            }
        })

        binding.edLoginPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.tilLoginPassword.error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilLoginPassword.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                binding.tilLoginPassword.error = if (s.isNullOrEmpty()) {
                    binding.btnLogin.isEnabled = false
                    getString(R.string.error_empty_password)
                } else if (s.length < 5 && s.isNotEmpty()) {
                    binding.btnLogin.isEnabled = false
                    getString(R.string.error_password)
                } else {
                    binding.btnLogin.isEnabled = true
                    null
                }
            }
        })

        return !(binding.edLoginEmail.error != null || binding.edLoginPassword.error != null)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }
}
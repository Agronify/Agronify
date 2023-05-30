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
import com.agronify.android.databinding.ActivityRegisterBinding
import com.agronify.android.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val registerViewModel: RegisterViewModel by viewModels()

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
        val valid = validateRegister()
        var isNavigatedToLogin = false
        binding.apply {
            btnRegister.setOnClickListener {
                registerViewModel.apply {
                    isLoading.observe(this@RegisterActivity) {
                        if (!it && error.value.isNullOrEmpty()) {
                            cpiRegister.visibility = View.GONE
                            if (!isNavigatedToLogin) {
                                isNavigatedToLogin = true
                                navigateToLogin()
                            }
                        } else if(!error.value.isNullOrEmpty()) {
                            cpiRegister.visibility = View.GONE
                            showToast(error.value!!)
                        } else {
                            cpiRegister.visibility = View.VISIBLE
                        }
                    }

                    if (valid && !edRegisterName.text.isNullOrEmpty() && !edRegisterEmail.text.isNullOrEmpty() && !edRegisterPassword.text.isNullOrEmpty() && !edRegisterConfirmPassword.text.isNullOrEmpty()) {
                        userRegister(
                            edRegisterName.text.toString(),
                            edRegisterEmail.text.toString(),
                            edRegisterPassword.text.toString()
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
            tvLogin.setOnClickListener {
                Intent(this@RegisterActivity, LoginActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            }
        }
    }

    private fun navigateToLogin() {
        Intent(this@RegisterActivity, LoginActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    private fun validateRegister(): Boolean {
        binding.edRegisterName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.tilRegisterName.error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilRegisterName.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                binding.tilRegisterName.error = if (s.isNullOrEmpty()) {
                    binding.btnRegister.isEnabled = false
                    getString(R.string.error_empty_name)
                } else {
                    binding.btnRegister.isEnabled = true
                    null
                }
            }
        })

        binding.edRegisterEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.tilRegisterEmail.error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilRegisterEmail.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                binding.tilRegisterEmail.error = if (s.isNullOrEmpty()) {
                    binding.btnRegister.isEnabled = false
                    getString(R.string.error_empty_email)
                } else if (!Patterns.EMAIL_ADDRESS.matcher(s).matches() && s.isNotEmpty()) {
                    binding.btnRegister.isEnabled = false
                    getString(R.string.error_invalid_email)
                } else {
                    binding.btnRegister.isEnabled = true
                    null
                }
            }
        })

        binding.edRegisterPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.tilRegisterPassword.error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilRegisterPassword.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                binding.tilRegisterPassword.error = if (s.isNullOrEmpty()) {
                    binding.btnRegister.isEnabled = false
                    getString(R.string.error_empty_password)
                } else if (s.length < 5 && s.isNotEmpty()) {
                    binding.btnRegister.isEnabled = false
                    getString(R.string.error_password)
                } else {
                    binding.btnRegister.isEnabled = true
                    null
                }
            }
        })

        binding.edRegisterConfirmPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.tilRegisterConfirmPassword.error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilRegisterConfirmPassword.error = null
            }

            override fun afterTextChanged(s: Editable?) {
                binding.tilRegisterConfirmPassword.error = if (s.isNullOrEmpty()) {
                    binding.btnRegister.isEnabled = false
                    getString(R.string.error_empty_password)
                } else if (s.toString() != binding.edRegisterPassword.text.toString()) {
                    binding.btnRegister.isEnabled = false
                    getString(R.string.error_confirm_password)
                } else {
                    binding.btnRegister.isEnabled = true
                    null
                }
            }
        })

        return !(binding.tilRegisterName.error != null || binding.tilRegisterEmail.error != null || binding.tilRegisterPassword.error != null || binding.tilRegisterConfirmPassword.error != null)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }
}
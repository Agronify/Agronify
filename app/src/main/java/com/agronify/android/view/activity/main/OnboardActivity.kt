package com.agronify.android.view.activity.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.agronify.android.databinding.ActivityOnboardBinding
import com.agronify.android.viewmodel.OnboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityOnboardBinding.inflate(layoutInflater)
    }
    private val onboardViewModel: OnboardViewModel by viewModels()
    private var newUser: Boolean = false
    private var hasLoaded: Boolean = false
    private var hasPermission: Boolean = false

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermission = when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> true
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> true
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplash()
        setContentView(binding.root)
        setupView()
        setupAction()
    }

    private fun setupView() {
        hideStatusBar()
        checkUser()
    }

    private fun setupAction() {
        binding.apply {
            btnSkip.setOnClickListener {
                navigateToMain()
            }

            btnLogin.setOnClickListener {
                navigateToLogin()
            }

            btnRegister.setOnClickListener {
                navigateToRegister()
            }
        }
    }

    private fun installSplash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen().apply {
                setKeepOnScreenCondition {
                    !hasLoaded && !hasPermission
                }

                setOnExitAnimationListener {
                    if (newUser && hasLoaded && hasPermission) {
                        it.remove()
                    } else if (!newUser && hasLoaded && hasPermission) {
                        navigateToMain()
                    }
                }
            }
        } else {
            if (!newUser && hasLoaded) {
                if (hasPermission) {
                    navigateToMain()
                } else {
                    requestLocationPermission()
                }
            }
        }
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    private fun requestLocationPermission() {
        val granted = LOCATION_PERMISSION.all {
            ContextCompat.checkSelfPermission(applicationContext, it) == PackageManager.PERMISSION_GRANTED
        }

        if (granted) {
            hasPermission = true
        } else {
            val shouldShowRationale = LOCATION_PERMISSION.any {
                ActivityCompat.shouldShowRequestPermissionRationale(this, it)
            }

            if (shouldShowRationale) {
                hasPermission = false
            } else {
                hasPermission = false
                locationPermissionRequest.launch(LOCATION_PERMISSION)
            }
        }
    }

    private fun checkUser() {
        onboardViewModel.apply {
            isNew.observe(this@OnboardActivity) {
                newUser = it
            }

            isLoading.observe(this@OnboardActivity) {
                hasLoaded = !it
                requestLocationPermission()
            }
        }
    }

    private fun setUser() {
        onboardViewModel.setNewUser(false)
    }

    private fun navigateToMain() {
        setUser()
        Intent(this@OnboardActivity, MainActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    private fun navigateToLogin() {
        Intent(this@OnboardActivity, LoginActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun navigateToRegister() {
        Intent(this@OnboardActivity, RegisterActivity::class.java).also {
            startActivity(it)
        }
    }

    private companion object {
        val LOCATION_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        } else {
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }
}
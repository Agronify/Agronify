package com.agronify.android.view.activity.main

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.agronify.android.R
import com.agronify.android.databinding.ActivityMainBinding
import com.agronify.android.util.NavigationCallback
import com.agronify.android.view.fragment.agro.edu.EduFragment
import com.agronify.android.view.fragment.agro.home.HomeFragment
import com.agronify.android.view.fragment.agro.profile.ProfileFragment
import com.agronify.android.view.fragment.agro.scan.ScanFragment
import com.agronify.android.viewmodel.MainViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationCallback {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var userToken: String
    private lateinit var userName: String
    private var hasLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
    }

    override fun onNavigationItemSelected(itemId: Int) {
        binding.bottomNavigationView.selectedItemId = itemId
    }

    private fun setupView() {
        checkUser()
    }

    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.mainFragment.id, fragment)
            .commit()
    }

    private fun checkUser() {
        mainViewModel.apply {
            isLoading.observe(this@MainActivity) {
                if (!it && isLogin.value != null && token.value != null) {
                    hasLoggedIn = isLogin.value!!
                    userToken = token.value!!
                    userName = name.value!!
                    setupFeature()
                }
            }

            checkLogin()
        }
    }

    private fun setupFeature() {
        navigateToFragment(HomeFragment.newInstance(hasLoggedIn, userToken, userName))
        binding.apply {
            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.home -> navigateToFragment(HomeFragment.newInstance(hasLoggedIn, userToken, userName))
                    R.id.agro_edu -> navigateToFragment(EduFragment())
                    R.id.agro_hub -> showSoonDialogHub()
                    R.id.profile -> navigateToFragment(ProfileFragment.newInstance(hasLoggedIn))
                }
                true
            }

            fabAgroScan.setOnClickListener {
                navigateToFragment(ScanFragment.newInstance(hasLoggedIn, userToken))
                onNavigationItemSelected(R.id.agro_scan)
            }
        }
    }

    fun showLoginDialog() {
        Dialog(this).apply {
            setContentView(R.layout.dialog_login)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            findViewById<MaterialButton>(R.id.btn_dialog_login).setOnClickListener {
                Intent(this@MainActivity, LoginActivity::class.java).apply {
                    startActivity(this)
                }
                dismiss()
            }
            show()
        }
    }

    fun showLogoutDialog() {
        Dialog(this).apply {
            setContentView(R.layout.dialog_logout)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            findViewById<MaterialButton>(R.id.btn_dialog_logout).setOnClickListener {
                mainViewModel.userLogout()
                Intent(this@MainActivity, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
                dismiss()
            }
            findViewById<TextView>(R.id.tv_dialog_back).setOnClickListener {
                dismiss()
            }
            show()
        }
    }

    fun showSoonDialog() {
        Dialog(this).apply {
            setContentView(R.layout.dialog_soon)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }

    private fun showSoonDialogHub() {
        Dialog(this).apply {
            setContentView(R.layout.dialog_soon)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setOnDismissListener { onNavigationItemSelected(R.id.home) }
            show()
        }
    }
}
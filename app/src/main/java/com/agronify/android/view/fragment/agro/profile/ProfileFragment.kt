package com.agronify.android.view.fragment.agro.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.agronify.android.databinding.FragmentProfileBinding
import com.agronify.android.model.remote.response.Auth
import com.agronify.android.util.Constants.EXTRA_LOGIN
import com.agronify.android.view.activity.main.LoginActivity
import com.agronify.android.view.activity.main.MainActivity
import com.agronify.android.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val binding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }
    private val profileViewModel: ProfileViewModel by viewModels()

    private var hasLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hasLoggedIn = it.getBoolean(EXTRA_LOGIN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        checkUser()
    }

    private fun checkUser() {
        if (hasLoggedIn) {
            profileViewModel.apply {
                isLoading.observe(viewLifecycleOwner) {
                    if (!it) {
                        val account = user.value
                        if (account != null) {
                            if (account.name != "" && account.email != "") {
                                showLogin(account)
                            } else {
                                showNotLogin()
                            }
                        }
                    }
                }

                getUser()
            }
        } else {
            showNotLogin()
        }
    }

    private fun showLogin(account: Auth) {
        binding.apply {
            cpiProfile.visibility = View.GONE
            profileMain.visibility = View.VISIBLE
            tvName.text = account.name
            tvEmail.text = account.email
            cvPassword.visibility = View.VISIBLE
            cvLogout.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    userLogout()
                }
            }
        }
    }

    private fun showNotLogin() {
        binding.apply {
            cpiProfile.visibility = View.GONE
            profileAlt.visibility = View.VISIBLE
            btnLogin.setOnClickListener {
                Intent(requireActivity(), LoginActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun userLogout() {
        profileViewModel.userLogout()
        navigateToMain()
    }

    private fun navigateToMain() {
        Intent(requireActivity(), MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(hasLoggedIn: Boolean) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_LOGIN, hasLoggedIn)
                }
            }
    }
}
package com.agronify.android.view.fragment.agro.scan

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agronify.android.BuildConfig.BUCKET_URL
import com.agronify.android.databinding.FragmentScanBinding
import com.agronify.android.util.Constants.DISEASE_PATH
import com.agronify.android.util.Constants.EXTRA_LOGIN
import com.agronify.android.util.Constants.EXTRA_TOKEN
import com.agronify.android.util.Constants.RIPENESS_PATH
import com.agronify.android.view.activity.agro.scan.ScanDiseaseActivity
import com.agronify.android.view.activity.agro.scan.ScanRipeActivity
import com.agronify.android.view.activity.main.MainActivity
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanFragment : Fragment() {
    private val binding by lazy {
        FragmentScanBinding.inflate(layoutInflater)
    }
    private var hasLoggedIn: Boolean = false
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hasLoggedIn = it.getBoolean(EXTRA_LOGIN)
            token = it.getString(EXTRA_TOKEN).toString()
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
        binding.apply {
            Glide.with(requireContext())
                .load(BUCKET_URL + DISEASE_PATH)
                .into(ivDisease)

            Glide.with(requireContext())
                .load(BUCKET_URL + RIPENESS_PATH)
                .into(ivRipeness)

            cvRipeness.setOnClickListener {
                if (hasLoggedIn) {
                    Intent(requireContext(), ScanRipeActivity::class.java).also {
                        it.putExtra(EXTRA_TOKEN, token)
                        startActivity(it)
                    }
                } else {
                    (requireActivity() as MainActivity).showLoginDialog()
                }
            }

            cvDisease.setOnClickListener {
                if (hasLoggedIn) {
                    Intent(requireContext(), ScanDiseaseActivity::class.java).also {
                        it.putExtra(EXTRA_TOKEN, token)
                        startActivity(it)
                    }
                } else {
                    (requireActivity() as MainActivity).showLoginDialog()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(hasLoggedIn: Boolean, token: String) =
            ScanFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_LOGIN, hasLoggedIn)
                    putString(EXTRA_TOKEN, token)
                }
            }
    }
}
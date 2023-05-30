package com.agronify.android.view.fragment.agro.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.agronify.android.R
import com.agronify.android.databinding.FragmentScanBinding
import com.agronify.android.util.Constants.EXTRA_LOGIN
import com.agronify.android.util.Constants.EXTRA_TOKEN
import com.agronify.android.viewmodel.ScanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanFragment : Fragment() {
    private val binding by lazy {
        FragmentScanBinding.inflate(layoutInflater)
    }
    private val scanViewModel: ScanViewModel by viewModels()
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
package com.agronify.android.view.fragment.agro.hub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agronify.android.databinding.FragmentHubBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HubFragment : Fragment() {
    private val binding by lazy {
        FragmentHubBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}
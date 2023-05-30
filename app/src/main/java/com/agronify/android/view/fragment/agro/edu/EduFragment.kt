package com.agronify.android.view.fragment.agro.edu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.agronify.android.databinding.FragmentEduBinding
import com.agronify.android.viewmodel.EduViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EduFragment : Fragment() {
    private val binding by lazy {
        FragmentEduBinding.inflate(layoutInflater)
    }
    private val eduViewModel: EduViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
package com.agronify.android.viewmodel

import androidx.lifecycle.ViewModel
import com.agronify.android.model.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EduViewModel @Inject constructor(
    private val eduRepository: EduRepository
) : ViewModel() {

}
package com.agronify.android.viewmodel

import androidx.lifecycle.ViewModel
import com.agronify.android.model.repository.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanRepository: ScanRepository
): ViewModel() {

}
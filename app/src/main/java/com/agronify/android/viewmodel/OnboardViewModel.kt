package com.agronify.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agronify.android.model.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isNew = MutableLiveData<Boolean>()
    val isNew: LiveData<Boolean> = _isNew

    init {
        checkUser()
    }

    private fun checkUser() {
        _isLoading.value = true
        viewModelScope.launch {
            _isNew.value = authRepository.getNew()
            _isLoading.value = false
        }
    }

    fun setNewUser(state: Boolean) {
        viewModelScope.launch {
            authRepository.saveNew(state)
        }
    }
}
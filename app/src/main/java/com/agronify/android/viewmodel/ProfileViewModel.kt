package com.agronify.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agronify.android.model.remote.response.Auth
import com.agronify.android.model.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _user = MutableLiveData<Auth>()
    val user: LiveData<Auth> = _user

    fun getUser() {
        _isLoading.value = true
        viewModelScope.launch {
            runBlocking {
                _user.value = authRepository.getUser()
            }
            _isLoading.value = false
        }
    }
}
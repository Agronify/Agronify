package com.agronify.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agronify.android.model.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    init {
        checkLogin()
    }

    fun checkLogin() = viewModelScope.launch {
        _isLoading.value = true
        runBlocking {
            val userToken = authRepository.getToken()
            _token.value = userToken
            _isLogin.value = userToken.isNotEmpty()

            if (userToken.isNotEmpty()) {
                val user = authRepository.getUser()
                _name.value = user.name
            } else {
                _name.value = ""
            }
        }
        _isLoading.value = false
    }
}
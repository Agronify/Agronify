package com.agronify.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agronify.android.model.repository.AuthRepository
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun userLogin(
        email: String,
        password: String
    ) = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null

        val request = JsonObject().apply {
            addProperty("email", email)
            addProperty("password", password)
        }

        runCatching {
            authRepository.userLogin(request)
        }.let { result ->
            result.onSuccess {
                val token = it.token
                authRepository.saveToken(token)
                authRepository.saveUser(it.name, it.email)
                _isLoading.value = false
            }

            result.onFailure { e ->
                if (e is HttpException) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    val errorMessage = errorResponse?.let { JSONObject(it).getString("error") }
                    _error.value = errorMessage
                }
                _isLoading.value = false
            }
        }
    }

    fun setNewUser(state: Boolean) {
        viewModelScope.launch {
            authRepository.saveNew(state)
        }
    }
}
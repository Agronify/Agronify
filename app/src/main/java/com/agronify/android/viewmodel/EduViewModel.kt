package com.agronify.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agronify.android.model.remote.response.Edu
import com.agronify.android.model.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class EduViewModel @Inject constructor(
    private val eduRepository: EduRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _edu = MutableLiveData<List<Edu>>()
    val edu: LiveData<List<Edu>> = _edu

    fun getEdu() = viewModelScope.launch {
        _isLoading.value = true

        runCatching {
            eduRepository.getEdu()
        }.let { result ->
            result.onSuccess {
                _edu.value = it
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

    fun searchEdu(query: String): List<Edu> {
        return _edu.value?.filter {
            it.title.contains(query, true) || it.tags.any { tag -> tag.contains(query, true) }
        } ?: emptyList()
    }
}
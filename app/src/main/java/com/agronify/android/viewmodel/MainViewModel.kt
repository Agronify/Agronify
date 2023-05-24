package com.agronify.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agronify.android.model.remote.response.CurrentWeather
import com.agronify.android.model.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
): ViewModel() {
    private val _currentWeather = MutableLiveData<CurrentWeather>()
    val currentWeather: LiveData<CurrentWeather> = _currentWeather

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getCurrentWeather(
        lat: Float,
        lon: Float
    ) = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null

        runCatching {
            weatherRepository.getCurrentWeather(lat, lon)
        }.let { result ->
            result.onSuccess {
                _isLoading.value = false
                _currentWeather.value = it.weather
            }

            result.onFailure { e ->
                if (e is HttpException) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    val errorMessage = errorResponse?.let { JSONObject(it).getString("message") }
                    _error.value = errorMessage
                }
                _isLoading.value = false
            }
        }
    }
}
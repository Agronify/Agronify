package com.agronify.android.model.repository

import com.agronify.android.model.remote.response.CurrentWeatherResponse
import com.agronify.android.model.remote.response.ForecastWeatherResponse
import com.agronify.android.model.remote.retrofit.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: ApiService,
    @Named("networkDispatcher") private val networkDispatcher: CoroutineDispatcher
){
    suspend fun getCurrentWeather(
        lat: Float,
        lon: Float
    ): CurrentWeatherResponse {
        return withContext(networkDispatcher) {
            apiService.getCurrentWeather(CURRENT, lat, lon, TIMEZONE).await()
        }
    }

    suspend fun getForecastWeather(
        lat: Float,
        lon: Float
    ): ForecastWeatherResponse {
        return withContext(networkDispatcher) {
            apiService.getForecastWeather(FORECAST, lat, lon, TIMEZONE).await()
        }
    }

    companion object {
        private const val CURRENT = "current"
        private const val FORECAST = "forecast"
        private const val TIMEZONE = "Asia/Jakarta"
    }
}
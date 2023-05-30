package com.agronify.android.model.remote.retrofit

import com.agronify.android.model.remote.response.Auth
import com.agronify.android.model.remote.response.CurrentWeatherResponse
import com.agronify.android.model.remote.response.ForecastWeatherResponse
import com.agronify.android.model.remote.response.LoginResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("auth/signin")
    fun userLogin(
        @Body request: JsonObject
    ): Call<LoginResponse>

    @POST("auth/signup")
    fun userRegister(
        @Body request: JsonObject
    ): Call<Auth>

    @GET("weather")
    fun getCurrentWeather(
        @Query("type") type: String,
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
        @Query("tz") tz: String
    ): Call<CurrentWeatherResponse>

    @GET("weather")
    fun getForecastWeather(
        @Query("type") type: String,
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
        @Query("tz") tz: String
    ): Call<ForecastWeatherResponse>
}
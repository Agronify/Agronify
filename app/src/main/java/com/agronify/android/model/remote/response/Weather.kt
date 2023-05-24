package com.agronify.android.model.remote.response

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
    @field:SerializedName("current_weather")
    val weather: CurrentWeather
)

data class CurrentWeather(
    @field:SerializedName("weathercode")
    val code: Int,

    @field:SerializedName("temperature")
    val temperature: Float
)

data class ForecastWeatherResponse(
    @field:SerializedName("daily")
    val weather: ForecastWeather
)

data class ForecastWeather(
    @field:SerializedName("weathercode")
    val code: List<Int>,

    @field:SerializedName("temperature_2m_min")
    val min: List<Float>,

    @field:SerializedName("temperature_2m_max")
    val max: List<Float>,

    @field:SerializedName("precipitation_probability_max")
    val precipitation: List<Int>,

    @field:SerializedName("windspeed_10m_max")
    val wind: List<Float>
)
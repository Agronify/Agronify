package com.agronify.android.util

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.agronify.android.R

object WeatherUtil {
    fun setWeather(context: Context, code: Int, tvWeatherType: TextView, ivWeather: ImageView) {
        when (code) {
            0 -> {
                tvWeatherType.text = context.getString(R.string.weather_clear)
                ivWeather.setImageResource(R.drawable.ic_weather_clear)
            }
            in 1..2 -> {
                tvWeatherType.text = context.getString(R.string.weather_partly_cloudy)
                ivWeather.setImageResource(R.drawable.ic_weather_partly_cloudy)
            }
            3 -> {
                tvWeatherType.text = context.getString(R.string.weather_overcast)
                ivWeather.setImageResource(R.drawable.ic_weather_overcast)
            }
            in 51..55 -> {
                tvWeatherType.text = context.getString(R.string.weather_drizzle)
                ivWeather.setImageResource(R.drawable.ic_weather_drizzle)
            }
            in 61..82 -> {
                tvWeatherType.text = context.getString(R.string.weather_rain)
                ivWeather.setImageResource(R.drawable.ic_weather_rain)
            }
            in 95..99 -> {
                tvWeatherType.text = context.getString(R.string.weather_thunderstorm)
                ivWeather.setImageResource(R.drawable.ic_weather_thunderstorm)
            }
            else -> {
                tvWeatherType.text = context.getString(R.string.weather_cloudy)
                ivWeather.setImageResource(R.drawable.ic_weather_cloudy)
            }
        }
    }
}
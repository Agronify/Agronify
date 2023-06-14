package com.agronify.android.util

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.agronify.android.R
import com.agronify.android.model.local.dummy.Tips
import com.agronify.android.util.DateUtil.getDayOrNight

object WeatherUtil {
    private fun setWeatherType(code: Int): String {
        return when (code) {
            0 -> "clear"
            in 1..2 -> "partly_cloudy"
            3 -> "overcast"
            in 51..55 -> "drizzle"
            in 61..82 -> "rain"
            in 95..99 -> "thunderstorm"
            else -> "cloudy"
        }
    }

    fun setCurrentWeather(context: Context, code: Int, tvWeatherType: TextView, ivWeather: ImageView) {
        val day = getDayOrNight() == "day"
        when (setWeatherType(code)) {
            "clear" -> {
                tvWeatherType.text = context.getString(R.string.weather_clear)
                if (day) ivWeather.setImageResource(R.drawable.ic_weather_clear) else ivWeather.setImageResource(R.drawable.ic_weather_clear_night)
            }
            "partly_cloudy" -> {
                tvWeatherType.text = context.getString(R.string.weather_partly_cloudy)
                if (day) ivWeather.setImageResource(R.drawable.ic_weather_partly_cloudy) else ivWeather.setImageResource(R.drawable.ic_weather_partly_cloudy_night)
            }
            "overcast" -> {
                tvWeatherType.text = context.getString(R.string.weather_overcast)
                ivWeather.setImageResource(R.drawable.ic_weather_overcast)
            }
            "drizzle" -> {
                tvWeatherType.text = context.getString(R.string.weather_drizzle)
                ivWeather.setImageResource(R.drawable.ic_weather_drizzle)
            }
            "rain" -> {
                tvWeatherType.text = context.getString(R.string.weather_rain)
                ivWeather.setImageResource(R.drawable.ic_weather_rain)
            }
            "thunderstorm" -> {
                tvWeatherType.text = context.getString(R.string.weather_thunderstorm)
                ivWeather.setImageResource(R.drawable.ic_weather_thunderstorm)
            }
            else -> {
                tvWeatherType.text = context.getString(R.string.weather_cloudy)
                ivWeather.setImageResource(R.drawable.ic_weather_cloudy)
            }
        }
    }

    fun setForecastWeather(context: Context, code: Int, tvWeatherType: TextView, ivWeather: ImageView, tips: List<Tips>, tvTips: TextView) {
        val tipsList = tips.filter { it.weather == setWeatherType(code) }
        val randomTips = tipsList[(tipsList.indices).random()].tips.random()
        tvTips.text = randomTips.content

        when (setWeatherType(code)) {
            "clear" -> {
                tvWeatherType.text = context.getString(R.string.weather_clear)
                ivWeather.setImageResource(R.drawable.ic_weather_clear)
            }
            "partly_cloudy" -> {
                tvWeatherType.text = context.getString(R.string.weather_partly_cloudy)
                ivWeather.setImageResource(R.drawable.ic_weather_partly_cloudy)
            }
            "overcast" -> {
                tvWeatherType.text = context.getString(R.string.weather_overcast)
                ivWeather.setImageResource(R.drawable.ic_weather_overcast)
            }
            "drizzle" -> {
                tvWeatherType.text = context.getString(R.string.weather_drizzle)
                ivWeather.setImageResource(R.drawable.ic_weather_drizzle)
            }
            "rain" -> {
                tvWeatherType.text = context.getString(R.string.weather_rain)
                ivWeather.setImageResource(R.drawable.ic_weather_rain)
            }
            "thunderstorm" -> {
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
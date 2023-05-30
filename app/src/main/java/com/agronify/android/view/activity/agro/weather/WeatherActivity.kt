package com.agronify.android.view.activity.agro.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.agronify.android.R
import com.agronify.android.databinding.ActivityWeatherBinding
import com.agronify.android.model.remote.response.ForecastWeather
import com.agronify.android.util.Constants.DEFAULT_LAT
import com.agronify.android.util.Constants.DEFAULT_LON
import com.agronify.android.util.DateUtil.getCurrentDate
import com.agronify.android.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityWeatherBinding.inflate(layoutInflater)
    }
    private val weatherViewModel: WeatherViewModel by viewModels()
    private var userLat: Double? = null
    private var userLon: Double? = null
    private var userLocation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupAction()
    }

    private fun setupView() {
        userLat = intent.getDoubleExtra(EXTRA_LAT, DEFAULT_LAT)
        userLon = intent.getDoubleExtra(EXTRA_LON, DEFAULT_LON)
        userLocation = intent.getStringExtra(EXTRA_LOCATION)

        setupAppBar()
        getDate()
    }

    private fun setupAction() {
        getWeather()
    }

    private fun setupAppBar() {
        binding.topAppBar.apply {
            setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun getDate() {
        val date = getCurrentDate()
        binding.tvDate.text = date
    }

    private fun getWeather() {
        val lat = userLat?.toFloat()
        val lon = userLon?.toFloat()

        if (lat != null && lon != null) {
            weatherViewModel.apply {
                getForecastWeather(lat, lon).also {
                    forecastWeather.observe(this@WeatherActivity) {
                        if (it != null) {
                            showForecastWeather(it)
                        }
                    }

                    isLoading.observe(this@WeatherActivity) {
                        binding.apply {
                            cpiWeather.visibility = if (it) View.VISIBLE else View.GONE
                        }
                    }

                    error.observe(this@WeatherActivity) {

                    }
                }
            }
        }
    }

    private fun showForecastWeather(forecastWeather: ForecastWeather) {
        binding.apply {
            tvWeatherLocation.text = userLocation
            for (i in 0..2) {
                when (i) {
                    0 -> {
                        when (forecastWeather.code[i]) {
                            0 -> {
                                tvWeatherTodayType.text = getString(R.string.weather_clear)
                                ivWeatherToday.setImageResource(R.drawable.ic_weather_clear)
                            }
                            1 -> {
                                tvWeatherTodayType.text = getString(R.string.weather_mainly_clear)
                                ivWeatherToday.setImageResource(R.drawable.ic_weather_mainly_clear)
                            }
                            2 -> {
                                tvWeatherTodayType.text = getString(R.string.weather_partly_cloudy)
                                ivWeatherToday.setImageResource(R.drawable.ic_weather_partly_cloudy)
                            }
                            3 -> {
                                tvWeatherTodayType.text = getString(R.string.weather_overcast)
                                ivWeatherToday.setImageResource(R.drawable.ic_weather_overcast)
                            }
                            51, 53, 55 -> {
                                tvWeatherTodayType.text = getString(R.string.weather_drizzle)
                                ivWeatherToday.setImageResource(R.drawable.ic_weather_drizzle)
                            }
                            61, 63, 65, 80, 81, 82 -> {
                                tvWeatherTodayType.text = getString(R.string.weather_rain)
                                ivWeatherToday.setImageResource(R.drawable.ic_weather_rain)
                            }
                            95, 96, 99 -> {
                                tvWeatherTodayType.text = getString(R.string.weather_thunderstorm)
                                ivWeatherToday.setImageResource(R.drawable.ic_weather_thunderstorm)
                            }
                            else -> {
                                tvWeatherTodayType.text = getString(R.string.weather_cloudy)
                                ivWeatherToday.setImageResource(R.drawable.ic_weather_cloudy)
                            }
                        }
                        tvWeatherTodayMin.text = forecastWeather.min[i].toInt().toString()
                        tvWeatherTodayMax.text = forecastWeather.max[i].toInt().toString()
                        tvWeatherTodayPrecipitation.text = forecastWeather.precipitation[i].toString()
                        tvWeatherTodayWind.text = forecastWeather.wind[i].toInt().toString()
                    }

                    1 -> {
                        when (forecastWeather.code[i]) {
                            0 -> {
                                tvWeatherTomorrowType.text = getString(R.string.weather_clear)
                                ivWeatherTomorrow.setImageResource(R.drawable.ic_weather_clear)
                            }
                            1 -> {
                                tvWeatherTomorrowType.text = getString(R.string.weather_mainly_clear)
                                ivWeatherTomorrow.setImageResource(R.drawable.ic_weather_mainly_clear)
                            }
                            2 -> {
                                tvWeatherTomorrowType.text = getString(R.string.weather_partly_cloudy)
                                ivWeatherTomorrow.setImageResource(R.drawable.ic_weather_partly_cloudy)
                            }
                            3 -> {
                                tvWeatherTomorrowType.text = getString(R.string.weather_overcast)
                                ivWeatherTomorrow.setImageResource(R.drawable.ic_weather_overcast)
                            }
                            51, 53, 55 -> {
                                tvWeatherTomorrowType.text = getString(R.string.weather_drizzle)
                                ivWeatherTomorrow.setImageResource(R.drawable.ic_weather_drizzle)
                            }
                            61, 63, 65, 80, 81, 82 -> {
                                tvWeatherTomorrowType.text = getString(R.string.weather_rain)
                                ivWeatherTomorrow.setImageResource(R.drawable.ic_weather_rain)
                            }
                            95, 96, 99 -> {
                                tvWeatherTomorrowType.text = getString(R.string.weather_thunderstorm)
                                ivWeatherTomorrow.setImageResource(R.drawable.ic_weather_thunderstorm)
                            }
                            else -> {
                                tvWeatherTomorrowType.text = getString(R.string.weather_cloudy)
                                ivWeatherTomorrow.setImageResource(R.drawable.ic_weather_cloudy)
                            }
                        }
                        tvWeatherTomorrowMin.text = forecastWeather.min[i].toInt().toString()
                        tvWeatherTomorrowMax.text = forecastWeather.max[i].toInt().toString()
                        tvWeatherTomorrowPrecipitation.text = forecastWeather.precipitation[i].toString()
                        tvWeatherTomorrowWind.text = forecastWeather.wind[i].toInt().toString()
                    }

                    2 -> {
                        when (forecastWeather.code[i]) {
                            0 -> {
                                tvWeatherAfterType.text = getString(R.string.weather_clear)
                                ivWeatherAfter.setImageResource(R.drawable.ic_weather_clear)
                            }
                            1 -> {
                                tvWeatherAfterType.text = getString(R.string.weather_mainly_clear)
                                ivWeatherAfter.setImageResource(R.drawable.ic_weather_mainly_clear)
                            }
                            2 -> {
                                tvWeatherAfterType.text = getString(R.string.weather_partly_cloudy)
                                ivWeatherAfter.setImageResource(R.drawable.ic_weather_partly_cloudy)
                            }
                            3 -> {
                                tvWeatherAfterType.text = getString(R.string.weather_overcast)
                                ivWeatherAfter.setImageResource(R.drawable.ic_weather_overcast)
                            }
                            51, 53, 55 -> {
                                tvWeatherAfterType.text = getString(R.string.weather_drizzle)
                                ivWeatherAfter.setImageResource(R.drawable.ic_weather_drizzle)
                            }
                            61, 63, 65, 80, 81, 82 -> {
                                tvWeatherAfterType.text = getString(R.string.weather_rain)
                                ivWeatherAfter.setImageResource(R.drawable.ic_weather_rain)
                            }
                            95, 96, 99 -> {
                                tvWeatherAfterType.text = getString(R.string.weather_thunderstorm)
                                ivWeatherAfter.setImageResource(R.drawable.ic_weather_thunderstorm)
                            }
                            else -> {
                                tvWeatherAfterType.text = getString(R.string.weather_cloudy)
                                ivWeatherAfter.setImageResource(R.drawable.ic_weather_cloudy)
                            }
                        }
                        tvWeatherAfterMin.text = forecastWeather.min[i].toInt().toString()
                        tvWeatherAfterMax.text = forecastWeather.max[i].toInt().toString()
                        tvWeatherAfterPrecipitation.text = forecastWeather.precipitation[i].toString()
                        tvWeatherAfterWind.text = forecastWeather.wind[i].toInt().toString()
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_LAT = "extra_lat"
        const val EXTRA_LON = "extra_lon"
        const val EXTRA_LOCATION = "extra_location"
    }
}
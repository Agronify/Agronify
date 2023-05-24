package com.agronify.android.view.activity.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.agronify.android.R
import com.agronify.android.databinding.ActivityMainBinding
import com.agronify.android.model.remote.response.CurrentWeather
import com.agronify.android.util.Constants.REQUEST_CODE_PERMISSIONS
import com.agronify.android.util.Constants.REQUIRED_PERMISSIONS
import com.agronify.android.view.activity.agro.edu.EduActivity
import com.agronify.android.view.activity.agro.hub.HubActivity
import com.agronify.android.view.activity.agro.profile.ProfileActivity
import com.agronify.android.view.activity.agro.scan.ScanActivity
import com.agronify.android.view.activity.agro.weather.WeatherActivity
import com.agronify.android.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private var userLat: Double? = null
    private var userLon: Double? = null
    private var userLocation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupAction()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (isPermissionGranted()) {
                getLastLocation()
            } else {
                finish()
            }
        }
    }

    private fun isPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupView() {
        if (!isPermissionGranted()) {
            ActivityCompat.requestPermissions(this@MainActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        setupFeature()
        setupBottomBar()
    }

    private fun setupAction() {
        getLastLocation()
    }

    private fun setupFeature() {
        binding.apply {
            btnAgroEdu.setOnClickListener {
                Intent(this@MainActivity, EduActivity::class.java).also {
                    startActivity(it)
                }
            }

            btnAgroScan.setOnClickListener {
                Intent(this@MainActivity, ScanActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun setupBottomBar() {
        binding.apply {
            bottomNavigationView.selectedItemId = R.id.home

            bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.agro_edu -> {
                        Intent(this@MainActivity, EduActivity::class.java).also {
                            startActivity(it)
                        }
                        false
                    }

                    R.id.agro_hub -> {
                        Intent(this@MainActivity, HubActivity::class.java).also {
                            startActivity(it)
                        }
                        false
                    }

                    R.id.profile -> {
                        Intent(this@MainActivity, ProfileActivity::class.java).also {
                            startActivity(it)
                        }
                        false
                    }

                    else -> false
                }
            }

            fabAgroScan.setOnClickListener {
                Intent(this@MainActivity, ScanActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun getWeather() {
        val lat = userLat?.toFloat()
        val lon = userLon?.toFloat()

        mainViewModel.apply {
            if (lat != null && lon != null) {
                getCurrentWeather(lat, lon).also {
                    currentWeather.observe(this@MainActivity) {
                        showCurrentWeather(it)
                    }

                    isLoading.observe(this@MainActivity) {
                        binding.apply {
                            cpiWeather.visibility = if (it) View.VISIBLE else View.GONE
                            tvWeatherDetail.visibility = if (it) View.GONE else View.VISIBLE
                        }
                    }

                    error.observe(this@MainActivity) {

                    }
                }
            }
        }
    }

    private fun getLastLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        geocoder = Geocoder(this@MainActivity, Locale.getDefault())

        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            return
        }

        val priority = PRIORITY_HIGH_ACCURACY
        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationProviderClient.getCurrentLocation(priority, cancellationTokenSource.token).addOnSuccessListener {
            userLat = it.latitude
            userLon = it.longitude
            userLocation = geocoder.getFromLocation(userLat!!, userLon!!, 1)?.get(0)?.locality
            getWeather()
        }
    }

    private fun showCurrentWeather(currentWeather: CurrentWeather) {
        binding.apply {
            tvWeatherLocation.text = userLocation
            tvWeatherTemperature.text = getString(R.string.weather_temp, currentWeather.temperature.toInt())
            when (currentWeather.code) {
                0 -> {
                    tvWeatherType.text = getString(R.string.weather_clear)
                    ivWeather.setImageResource(R.drawable.ic_weather_clear)
                }
                1 -> {
                    tvWeatherType.text = getString(R.string.weather_mainly_clear)
                    ivWeather.setImageResource(R.drawable.ic_weather_mainly_clear)
                }
                2 -> {
                    tvWeatherType.text = getString(R.string.weather_partly_cloudy)
                    ivWeather.setImageResource(R.drawable.ic_weather_partly_cloudy)
                }
                3 -> {
                    tvWeatherType.text = getString(R.string.weather_overcast)
                    ivWeather.setImageResource(R.drawable.ic_weather_overcast)
                }
                51, 53, 55 -> {
                    tvWeatherType.text = getString(R.string.weather_drizzle)
                    ivWeather.setImageResource(R.drawable.ic_weather_drizzle)
                }
                61, 63, 65, 80, 81, 82 -> {
                    tvWeatherType.text = getString(R.string.weather_rain)
                    ivWeather.setImageResource(R.drawable.ic_weather_rain)
                }
                95, 96, 99 -> {
                    tvWeatherType.text = getString(R.string.weather_thunderstorm)
                    ivWeather.setImageResource(R.drawable.ic_weather_thunderstorm)
                }
                else -> {
                    tvWeatherType.text = getString(R.string.weather_cloudy)
                    ivWeather.setImageResource(R.drawable.ic_weather_cloudy)
                }
            }
            clWeather.setOnClickListener {
                Intent(this@MainActivity, WeatherActivity::class.java).also {
                    it.putExtra(WeatherActivity.EXTRA_LAT, userLat)
                    it.putExtra(WeatherActivity.EXTRA_LON, userLon)
                    it.putExtra(WeatherActivity.EXTRA_LOCATION, userLocation)
                    startActivity(it)
                }
            }
        }
    }
}
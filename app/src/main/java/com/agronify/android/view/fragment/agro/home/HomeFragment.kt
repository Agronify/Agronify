package com.agronify.android.view.fragment.agro.home

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.agronify.android.BuildConfig.BUCKET_URL
import com.agronify.android.R
import com.agronify.android.databinding.FragmentHomeBinding
import com.agronify.android.model.remote.response.CurrentWeather
import com.agronify.android.model.remote.response.Edu
import com.agronify.android.util.Constants.EXTRA_EDU_CONTENT
import com.agronify.android.util.Constants.EXTRA_EDU_IMAGE
import com.agronify.android.util.Constants.EXTRA_EDU_TAGS
import com.agronify.android.util.Constants.EXTRA_EDU_TITLE
import com.agronify.android.util.Constants.EXTRA_LAT
import com.agronify.android.util.Constants.EXTRA_LOCATION
import com.agronify.android.util.Constants.EXTRA_LOGIN
import com.agronify.android.util.Constants.EXTRA_LON
import com.agronify.android.util.Constants.EXTRA_TOKEN
import com.agronify.android.view.activity.agro.edu.EduDetailActivity
import com.agronify.android.view.activity.agro.weather.WeatherActivity
import com.agronify.android.view.activity.main.MainActivity
import com.agronify.android.view.fragment.agro.edu.EduFragment
import com.agronify.android.view.fragment.agro.scan.ScanFragment
import com.agronify.android.viewmodel.HomeViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val binding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }
    private val homeViewModel: HomeViewModel by viewModels()
    private var hasLoggedIn: Boolean = false
    private lateinit var token: String

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    private var userLat: Double? = null
    private var userLon: Double? = null
    private var userLocation: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hasLoggedIn = it.getBoolean(EXTRA_LOGIN)
            token = it.getString(EXTRA_TOKEN).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLocationPermission()
        setupView()
    }

    private fun checkLocationPermission() {
        homeViewModel.apply {
            locationPermissionGranted.observe(requireActivity()) { isGranted ->
                if (!isGranted) {
                    requestPermission(requireActivity())
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())

        fusedLocationProviderClient.getCurrentLocation(LOCATION_PRIORITY, LOCATION_TOKEN).addOnSuccessListener {
            userLat = it.latitude
            userLon = it.longitude
            userLocation = geocoder.getFromLocation(userLat!!, userLon!!, 1)?.get(0)?.locality
            getCurrentWeather()
        }
    }

    private fun setupView() {
        getUserLocation()
        getSpotlight()
        setupFeature()
    }

    private fun getCurrentWeather() {
        val lat = userLat?.toFloat()
        val lon = userLon?.toFloat()

        if (lat != null && lon != null && isAdded) {
            homeViewModel.apply {
                getCurrentWeather(lat, lon).also {
                    currentWeather.observe(requireActivity()) {
                        showCurrentWeather(it)
                    }

                    isLoading.observe(requireActivity()) {
                        binding.apply {
                            cpiWeather.visibility = if (it) View.VISIBLE else View.GONE
                            tvWeatherDetail.visibility = if (it) View.GONE else View.VISIBLE
                        }
                    }

                    error.observe(requireActivity()) {}
                }
            }
        }
    }

    private fun showCurrentWeather(currentWeather: CurrentWeather) {
        binding.apply {
            tvWeatherDetail.text = getString(R.string.weather_tap_detail)
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

            cvWeather.setOnClickListener {
                Intent(requireActivity(), WeatherActivity::class.java).also {
                    it.putExtra(EXTRA_LAT, userLat)
                    it.putExtra(EXTRA_LON, userLon)
                    it.putExtra(EXTRA_LOCATION, userLocation)
                    startActivity(it)
                }
            }
        }
    }

    private fun getSpotlight() {
        binding.apply {
            homeViewModel.apply {
                isSpotlightLoading.observe(requireActivity()) {
                    if (!it) {
                        val spotlight = spotlight.value
                        if (spotlight != null) {
                            showSpotlight(spotlight)
                        }
                        cpiSpotlight.visibility = View.GONE
                    } else {
                        cpiSpotlight.visibility = View.VISIBLE
                    }
                }

                getSpotlight()
            }
        }
    }

    private fun showSpotlight(edu: Edu) {
        binding.apply {
            if (isAdded) {
                val image = edu.image
                Glide.with(requireActivity())
                    .load(BUCKET_URL + image)
                    .into(ivAgroSpotlight)
                ivAgroSpotlightOverlay.visibility = View.VISIBLE
                tvAgroSpotlightTitle.text = edu.title
                tvAgroSpotlightDesc.text = edu.content

                val tags: ArrayList<String> = arrayListOf()
                for (tag in edu.tags) {
                    tags.add(tag)
                }
                cvAgroSpotlight.setOnClickListener {
                    Intent(requireActivity(), EduDetailActivity::class.java).also {
                        it.putExtra(EXTRA_EDU_IMAGE, image)
                        it.putExtra(EXTRA_EDU_TITLE, edu.title)
                        it.putExtra(EXTRA_EDU_CONTENT, edu.content)
                        it.putExtra(EXTRA_EDU_TAGS, tags)
                        startActivity(it)
                    }
                }
            }
        }
    }

    private fun setupFeature() {
        binding.apply {
            btnAgroEdu.setOnClickListener {
                (requireActivity() as MainActivity).apply {
                    navigateToFragment(EduFragment())
                    onNavigationItemSelected(R.id.agro_edu)
                }
            }

            btnAgroScan.setOnClickListener {
                (requireActivity() as MainActivity).apply {
                    navigateToFragment(ScanFragment.newInstance(hasLoggedIn, token))
                    onNavigationItemSelected(R.id.agro_scan)
                }
            }

            val upcomingFeature = arrayOf(
                btnAgroHub,
                btnAgroMart,
                btnAgroWork,
                btnAgroPlan,
                btnAgroFin,
                btnAgroMore
            )

            upcomingFeature.forEach {
                it.setOnClickListener {
                    (requireActivity() as MainActivity).showSoonDialog()
                }
            }
        }
    }

    companion object {
        const val LOCATION_PRIORITY = Priority.PRIORITY_HIGH_ACCURACY
        val LOCATION_TOKEN = CancellationTokenSource().token

        @JvmStatic
        fun newInstance(hasLoggedIn: Boolean, token: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_LOGIN, hasLoggedIn)
                    putString(EXTRA_TOKEN, token)
                }
            }
    }
}
package com.agronify.android.view.fragment.agro.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.agronify.android.BuildConfig.BUCKET_URL
import com.agronify.android.R
import com.agronify.android.databinding.FragmentHomeBinding
import com.agronify.android.model.remote.response.CurrentWeather
import com.agronify.android.model.remote.response.Edu
import com.agronify.android.util.Constants.DEFAULT_LAT
import com.agronify.android.util.Constants.DEFAULT_LON
import com.agronify.android.util.Constants.EXTRA_EDU
import com.agronify.android.util.Constants.EXTRA_LAT
import com.agronify.android.util.Constants.EXTRA_LOCATION
import com.agronify.android.util.Constants.EXTRA_LOGIN
import com.agronify.android.util.Constants.EXTRA_LON
import com.agronify.android.util.Constants.EXTRA_NAME
import com.agronify.android.util.Constants.EXTRA_TOKEN
import com.agronify.android.util.DateUtil.getDayOrNight
import com.agronify.android.util.WeatherUtil.setCurrentWeather
import com.agronify.android.view.activity.agro.edu.EduDetailActivity
import com.agronify.android.view.activity.agro.weather.WeatherActivity
import com.agronify.android.view.activity.main.MainActivity
import com.agronify.android.view.activity.main.OnboardActivity
import com.agronify.android.view.fragment.agro.edu.EduFragment
import com.agronify.android.view.fragment.agro.scan.ScanFragment
import com.agronify.android.viewmodel.HomeViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val binding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }
    private val homeViewModel: HomeViewModel by viewModels()
    private var hasLoggedIn: Boolean = false
    private lateinit var token: String
    private lateinit var name: String

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    private var userLat: Double? = null
    private var userLon: Double? = null
    private var userLocation: String? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            (requireActivity() as MainActivity).apply {
                navigateToFragment(newInstance(hasLoggedIn, token, name))
            }
        } else {
            askPermission()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hasLoggedIn = it.getBoolean(EXTRA_LOGIN)
            token = it.getString(EXTRA_TOKEN).toString()
            name = it.getString(EXTRA_NAME).toString()
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
        setupView()
        setupAction()
    }

    private fun getUserLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())

        val granted = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            fusedLocationProviderClient.getCurrentLocation(LOCATION_PRIORITY, LOCATION_TOKEN).addOnSuccessListener {
                userLat = it.latitude
                userLon = it.longitude
                userLocation = geocoder.getFromLocation(userLat!!, userLon!!, 1)?.get(0)?.locality
                getCurrentWeather()
            }

            binding.cvWeather.setOnClickListener {
                navigateToWeather()
            }
        } else {
            userLat = DEFAULT_LAT
            userLon = DEFAULT_LON
            userLocation = geocoder.getFromLocation(DEFAULT_LAT, DEFAULT_LON, 1)?.get(0)?.countryName
            getCurrentWeather()

            binding.cvWeather.setOnClickListener {
                askPermission()
            }
        }
    }

    private fun setupView() {
        getUserLocation()
        getName()
        getSpotlight()
        setupFeature()
    }

    private fun setupAction() {
        onSwipeRefresh()
    }

    private fun requestLocationPermission() {
        val granted = OnboardActivity.LOCATION_PERMISSION.all {
            ContextCompat.checkSelfPermission(requireActivity(), it) == PackageManager.PERMISSION_GRANTED
        }

        if (!granted) locationPermissionRequest.launch(OnboardActivity.LOCATION_PERMISSION)
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
                            ivWeatherOverlay.visibility = if (it) View.GONE else View.VISIBLE
                            tvWeatherDetail.visibility = if (it) View.GONE else View.VISIBLE
                        }
                    }

                    error.observe(requireActivity()) {}
                }
            }
        }
    }

    private fun showCurrentWeather(currentWeather: CurrentWeather) {
        val current = getDayOrNight()

        binding.apply {
            tvWeatherDetail.text = getString(R.string.weather_tap_detail)
            tvWeatherLocation.text = userLocation
            tvWeatherTemperature.text = getString(R.string.weather_temp, currentWeather.temperature.toInt())

            runBlocking {
                when (current) {
                    "day" -> {
                        Glide.with(requireActivity())
                            .load(R.drawable.weather_day)
                            .into(ivWeatherBackground)
                    }
                    "night" -> {
                        Glide.with(requireActivity())
                            .load(R.drawable.weather_night)
                            .into(ivWeatherBackground)
                    }
                    else -> {
                        ivWeatherBackground.visibility = View.GONE
                        ivWeatherOverlay.visibility = View.GONE
                    }
                }
            }

            setCurrentWeather(requireContext(), currentWeather.code, tvWeatherType, ivWeather)
        }
    }

    private fun navigateToWeather() {
        Intent(requireActivity(), WeatherActivity::class.java).also {
            it.putExtra(EXTRA_LAT, userLat)
            it.putExtra(EXTRA_LON, userLon)
            it.putExtra(EXTRA_LOCATION, userLocation)
            startActivity(it)
        }
    }

    private fun getName() {
        binding.apply {
            tvGreeting.text = if (name != "") getString(R.string.greeting, name) else getString(R.string.greeting, "Agromates")
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

                cvAgroSpotlight.setOnClickListener {
                    Intent(requireActivity(), EduDetailActivity::class.java).also {
                        it.putExtra(EXTRA_EDU, edu)
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

    private fun onSwipeRefresh() {
        binding.swipeHome.setOnRefreshListener {
            (requireActivity() as MainActivity).apply {
                navigateToFragment(newInstance(hasLoggedIn, token, name))
            }
        }
    }

    private fun askPermission() {
        Snackbar.make(binding.root, getString(R.string.permission_location), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.permission_grant)) {
                requestLocationPermission()
            }
            .show()
    }

    companion object {
        const val LOCATION_PRIORITY = Priority.PRIORITY_HIGH_ACCURACY
        val LOCATION_TOKEN = CancellationTokenSource().token

        @JvmStatic
        fun newInstance(hasLoggedIn: Boolean, token: String, name: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(EXTRA_LOGIN, hasLoggedIn)
                    putString(EXTRA_TOKEN, token)
                    putString(EXTRA_NAME, name)
                }
            }
    }
}
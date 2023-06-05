package com.agronify.android.view.activity.agro.scan

import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.agronify.android.R
import com.agronify.android.databinding.ActivityScanAddBinding
import com.agronify.android.util.Constants.EXTRA_SCAN_ID
import com.agronify.android.util.Constants.EXTRA_SCAN_NAME
import com.agronify.android.util.Constants.EXTRA_SCAN_TYPE
import com.agronify.android.util.Constants.EXTRA_TOKEN
import com.agronify.android.viewmodel.ScanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanAddActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityScanAddBinding.inflate(layoutInflater)
    }
    private val scanViewModel: ScanViewModel by viewModels()
    private var launcherIntentCameraX: ActivityResultLauncher<Intent>? = null
    private var launcherIntentGallery: ActivityResultLauncher<Intent>? = null
    private var userToken: String = ""
    private var cropId: Int = 0
    private var cropName: String = ""
    private var cropType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkCameraPermission()
        setupView()
        setupAction()
    }

    private fun checkCameraPermission() {
        scanViewModel.apply {
            cameraPermissionGranted.observe(this@ScanAddActivity) { isGranted ->
                if (!isGranted) {
                    requestPermission(this@ScanAddActivity)
                }
            }
        }
    }

    private fun setupView() {
        userToken = intent.getStringExtra(EXTRA_TOKEN).toString()
        cropId = intent.getIntExtra(EXTRA_SCAN_ID, 0)
        cropName = intent.getStringExtra(EXTRA_SCAN_NAME).toString()
        cropType = intent.getStringExtra(EXTRA_SCAN_TYPE).toString()

        setupAppBar()
        setupLauncher()
        getImage()
    }

    private fun setupAppBar() {
        binding.apply {
            topAppBar.apply {
                title = getString(R.string.add_detect_title, cropName)
                setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
            }
        }
    }

    private fun setupLauncher() {
        launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            scanViewModel.handleCameraActivityResult(it)
        }

        launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            scanViewModel.handleGalleryActivityResult(it, applicationContext)
        }
    }

    private fun getImage() {
        binding.apply {
            scanViewModel.apply {
                isLoading.observe(this@ScanAddActivity) {

                }
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            btnCamera.setOnClickListener {
                Intent(this@ScanAddActivity, ScanCameraActivity::class.java).also {
                    launcherIntentCameraX?.launch(it)
                }
            }

            btnGallery.setOnClickListener {
                Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
                    launcherIntentGallery?.launch(it)
                }
            }

            btnDetect.apply {
                text = getString(R.string.add_detect_title, cropName)
                setOnClickListener {
                    scanViewModel.apply {

                    }
                }
            }
        }
    }
}
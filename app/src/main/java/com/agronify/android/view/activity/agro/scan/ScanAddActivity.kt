package com.agronify.android.view.activity.agro.scan

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.agronify.android.R
import com.agronify.android.databinding.ActivityScanAddBinding
import com.agronify.android.model.remote.response.PredictResponse
import com.agronify.android.util.Constants.EXTRA_SCAN_ID
import com.agronify.android.util.Constants.EXTRA_SCAN_NAME
import com.agronify.android.util.Constants.EXTRA_SCAN_RESULT
import com.agronify.android.util.Constants.EXTRA_SCAN_SUCCESS
import com.agronify.android.util.Constants.EXTRA_SCAN_TYPE
import com.agronify.android.util.Constants.EXTRA_TOKEN
import com.agronify.android.view.fragment.helper.ModalBottomSheet
import com.agronify.android.viewmodel.ScanViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

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
    private lateinit var imageFile: File
    private var imagePrediction: PredictResponse? = null
    private var isImageUploaded: Boolean? = false

    private val cameraPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            binding.btnCamera.isEnabled = true
        }
    }

    private val galleryPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            binding.btnGallery.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestPermission()
        setupView()
        setupAction()
    }

    private fun requestPermission() {
        val grantedCamera = ContextCompat.checkSelfPermission(applicationContext, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED
        val grantedGallery = ContextCompat.checkSelfPermission(applicationContext, GALLERY_PERMISSION) == PackageManager.PERMISSION_GRANTED

        if (!grantedCamera || !grantedGallery) {
            cameraPermissionRequest.launch(CAMERA_PERMISSION)
            galleryPermissionRequest.launch(GALLERY_PERMISSION)
        } else {
            binding.btnCamera.isEnabled = true
            binding.btnGallery.isEnabled = true
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
            imagePrediction = null
            scanViewModel.handleCameraActivityResult(it)
        }

        launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            imagePrediction = null
            scanViewModel.handleGalleryActivityResult(it, applicationContext)
        }
    }

    private fun getImage() {
        binding.apply {
            scanViewModel.apply {
                previewBitmap.observe(this@ScanAddActivity) {
                    ivImage.visibility = View.VISIBLE
                    ivImageAlt.visibility = View.INVISIBLE
                    Glide.with(this@ScanAddActivity)
                        .load(it)
                        .into(ivImage)
                }

                file.observe(this@ScanAddActivity) {
                    imageFile = it
                }
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            btnCamera.setOnClickListener {
                Intent(this@ScanAddActivity, ScanCameraActivity::class.java).also {
                    imagePrediction = null
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    launcherIntentCameraX?.launch(it)
                }
            }

            btnGallery.setOnClickListener {
                Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
                    imagePrediction = null
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    launcherIntentGallery?.launch(it)
                }
            }

            btnDetect.apply {
                text = getString(R.string.add_detect_title, cropName)
                setOnClickListener {
                    if (ivImage.drawable != null) {
                        if (imagePrediction == null) {
                            scanViewModel.apply {
                                isLoading.removeObservers(this@ScanAddActivity)

                                isLoading.observe(this@ScanAddActivity) {
                                    if (!it) {
                                        isImageUploaded = isUploaded.value

                                        if (isImageUploaded == true) {
                                            imagePrediction = prediction.value
                                            showResult(true)
                                        } else {
                                            showResult(false)
                                        }
                                    } else {
                                        cpiPredict.visibility = View.VISIBLE
                                    }
                                }

                                uploadScan(userToken, imageFile, cropId, cropType)
                            }
                        } else {
                            showResult(true)
                        }
                    } else {
                        showResult(false)
                    }
                }
            }
        }
    }

    private fun showResult(isSuccess: Boolean) {
        binding.cpiPredict.visibility = View.GONE

        val modalBottomSheet = ModalBottomSheet()
        modalBottomSheet.apply {
            arguments = Bundle().apply {
                putBoolean(EXTRA_SCAN_SUCCESS, isSuccess)
                putString(EXTRA_SCAN_TYPE, cropType)
                putParcelable(EXTRA_SCAN_RESULT, imagePrediction)
            }
            if (isImageUploaded == true) show(this@ScanAddActivity.supportFragmentManager, ModalBottomSheet.TAG)
        }
    }

    private companion object {
        const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        val GALLERY_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
}
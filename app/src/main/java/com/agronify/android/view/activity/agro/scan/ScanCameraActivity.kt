package com.agronify.android.view.activity.agro.scan

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.View
import android.view.WindowInsets
import androidx.activity.viewModels
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.agronify.android.databinding.ActivityScanCameraBinding
import com.agronify.android.viewmodel.ScanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanCameraActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityScanCameraBinding.inflate(layoutInflater)
    }
    private val scanViewModel: ScanViewModel by viewModels()
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var camera: CameraControl? = null
    private var orientationEventListener: OrientationEventListener? = null
    private var imageRotationDegreesValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
    }

    override fun onDestroy() {
        super.onDestroy()
        imageCapture = null
        orientationEventListener?.disable()
        ProcessCameraProvider.getInstance(this@ScanCameraActivity).get().unbindAll()
    }

    private fun setupView() {
        hideStatusBar()
        setupAppBar()
        setupCamera()
        setupOrientation()
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }

    private fun setupAppBar() {
        binding.apply {
            topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this@ScanCameraActivity)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.viewfinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this@ScanCameraActivity, cameraSelector, preview, imageCapture)
                camera = cameraProvider.bindToLifecycle(this@ScanCameraActivity, cameraSelector, preview, imageCapture).cameraControl
                binding.apply {
                    camera!!.enableTorch(true)
                    var useFlash = true
                    btnFlash.setOnClickListener {
                        useFlash = !useFlash
                        camera!!.enableTorch(useFlash)
                    }
                }
            } catch (_: Exception) {}
        }, ContextCompat.getMainExecutor(this@ScanCameraActivity))
    }

    private fun setupOrientation() {
        orientationEventListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                val rotation = when (orientation) {
                    in 45..134 -> 180
                    in 135..224 -> 270
                    in 225..314 -> 0
                    else -> 90
                }
                if (imageRotationDegreesValue != rotation) {
                    imageRotationDegreesValue = rotation
                    imageCapture?.targetRotation = rotation
                }
            }
        }
        orientationEventListener?.enable()
    }
}
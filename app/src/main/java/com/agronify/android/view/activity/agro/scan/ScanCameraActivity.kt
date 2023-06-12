package com.agronify.android.view.activity.agro.scan

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.View
import android.view.WindowInsets
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.agronify.android.R
import com.agronify.android.databinding.ActivityScanCameraBinding
import com.agronify.android.util.CameraUtil.createTempFile
import com.agronify.android.util.Constants.CAMERA_X_ROTATION
import com.agronify.android.util.Constants.CAMERA_X_SUCCESS
import com.agronify.android.util.Constants.EXTRA_SCAN_IMAGE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanCameraActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityScanCameraBinding.inflate(layoutInflater)
    }
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var camera: CameraControl? = null
    private var orientationEventListener: OrientationEventListener? = null
    private var imageRotationDegrees: Int = 0

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
        setupOrientation()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this@ScanCameraActivity)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.viewfinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this@ScanCameraActivity, cameraSelector, preview, imageCapture)
            camera = cameraProvider.bindToLifecycle(this@ScanCameraActivity, cameraSelector, preview, imageCapture).cameraControl
            binding.apply {
                var useFlash = false
                btnFlash.setOnClickListener {
                    useFlash = !useFlash
                    camera!!.enableTorch(useFlash)
                    binding.btnFlash.setImageResource(if (useFlash) R.drawable.ic_flash else R.drawable.ic_flash_alt)
                }

                fabScan.setOnClickListener {
                    capture()
                }
            }
        }, ContextCompat.getMainExecutor(this@ScanCameraActivity))
    }

    private fun setupOrientation() {
        orientationEventListener?.disable()
        orientationEventListener = object : OrientationEventListener(this@ScanCameraActivity) {
            override fun onOrientationChanged(orientation: Int) {
                imageRotationDegrees = when (orientation) {
                    in 45..134 -> 270
                    in 135..224 -> 180
                    in 225..314 -> 90
                    else -> 0
                }
            }
        }
        orientationEventListener?.enable()
    }

    private fun capture() {
        val imageCapture = imageCapture ?: return
        val imageFile = createTempFile(this@ScanCameraActivity)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this@ScanCameraActivity), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Intent().apply {
                    putExtra(EXTRA_SCAN_IMAGE, imageFile)
                    putExtra(CAMERA_X_ROTATION, imageRotationDegrees)
                    setResult(CAMERA_X_SUCCESS, this)
                    finish()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                showToast("Error: ${exception.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this@ScanCameraActivity, message, Toast.LENGTH_SHORT).show()
    }
}
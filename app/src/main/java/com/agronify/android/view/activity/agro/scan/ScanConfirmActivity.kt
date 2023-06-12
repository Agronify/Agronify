package com.agronify.android.view.activity.agro.scan

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.activity.viewModels
import com.agronify.android.databinding.ActivityScanConfirmBinding
import com.agronify.android.util.Constants.CAMERA_X_ROTATION
import com.agronify.android.util.Constants.EXTRA_SCAN_IMAGE
import com.agronify.android.viewmodel.ScanViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ScanConfirmActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityScanConfirmBinding.inflate(layoutInflater)
    }
    private val scanViewModel: ScanViewModel by viewModels()
    private var imageFile: File? = null
    private var imageRotationDegrees: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        imageFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_SCAN_IMAGE, File::class.java)
        } else {
            intent.getSerializableExtra(EXTRA_SCAN_IMAGE)
        } as? File
        imageRotationDegrees = intent.getIntExtra(CAMERA_X_ROTATION, 0)

        hideStatusBar()
        setupAppBar()
        getImage()
    }

    private fun setupAction() {
        binding.apply {
            btnConfirm.setOnClickListener {
                Intent(this@ScanConfirmActivity, ScanAddActivity::class.java).also {
                    it.putExtra(EXTRA_SCAN_IMAGE, imageFile)
                    startActivity(it)
                    finish()
                }
            }

            btnCancel.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
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

    private fun getImage() {
        scanViewModel.apply {

        }
    }
}
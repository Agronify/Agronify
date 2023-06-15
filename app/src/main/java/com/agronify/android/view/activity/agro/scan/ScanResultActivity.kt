package com.agronify.android.view.activity.agro.scan

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.agronify.android.BuildConfig.BUCKET_URL
import com.agronify.android.R
import com.agronify.android.databinding.ActivityScanResultBinding
import com.agronify.android.model.remote.response.PredictResponse
import com.agronify.android.util.Constants.EXTRA_SCAN_RESULT
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanResultActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityScanResultBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupAction()
    }

    private fun setupView() {
        setupAppBar()
        setScroll()

        val imagePrediction = intent.getParcelableExtra<PredictResponse>(EXTRA_SCAN_RESULT)
        binding.apply {
            Glide.with(this@ScanResultActivity)
                .load(BUCKET_URL + imagePrediction?.path)
                .into(ivImage)
            if (imagePrediction?.disease != null) {
                tvResultTitle.text = imagePrediction.disease.name
                tvAccuracy.text = getString(R.string.add_accuracy, imagePrediction.accuracy)
                tvResultDesc.text = imagePrediction.disease.description
            } else {
                tvResultTitle.text = imagePrediction?.result
                tvAccuracy.text = getString(R.string.add_accuracy, imagePrediction?.accuracy)
                tvResultDesc.visibility = View.GONE
            }
        }
    }

    private fun setupAction() {
        binding.fabShare.setOnClickListener {
            showSoonDialog()
        }
    }

    private fun setupAppBar() {
        binding.apply {
            topAppBar.apply {
                setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
            }
        }
    }

    private fun setScroll() {
        binding.apply {
            val threshold = (20 * resources.displayMetrics.density).toInt()
            val fabThreshold = (484 * resources.displayMetrics.density).toInt()
            nsvResult.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                if (scrollY > threshold) {
                    topAppBar.navigationIcon = ContextCompat.getDrawable(this@ScanResultActivity, R.drawable.ic_back_tint)
                    topAppBar.title = ""

                    if (scrollY > fabThreshold) {
                        fabShareScroll.show()
                    } else {
                        fabShareScroll.hide()
                    }
                } else {
                    topAppBar.navigationIcon = ContextCompat.getDrawable(this@ScanResultActivity, R.drawable.ic_back)
                    topAppBar.title = getString(R.string.result_title)
                }
            }
        }
    }

    private fun showSoonDialog() {
        Dialog(this).apply {
            setContentView(R.layout.dialog_soon)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }
}
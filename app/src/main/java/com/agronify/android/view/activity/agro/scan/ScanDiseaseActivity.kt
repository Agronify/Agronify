package com.agronify.android.view.activity.agro.scan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.agronify.android.R
import com.agronify.android.databinding.ActivityScanDiseaseBinding
import com.agronify.android.model.remote.response.Scan
import com.agronify.android.util.Constants.EXTRA_SCAN_ID
import com.agronify.android.util.Constants.EXTRA_SCAN_NAME
import com.agronify.android.util.Constants.EXTRA_SCAN_TYPE
import com.agronify.android.util.Constants.EXTRA_TOKEN
import com.agronify.android.view.adapter.ScanAdapter
import com.agronify.android.viewmodel.ScanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanDiseaseActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityScanDiseaseBinding.inflate(layoutInflater)
    }
    private val scanViewModel: ScanViewModel by viewModels()
    private var userToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
    }

    private fun setupView() {
        userToken = intent.getStringExtra(EXTRA_TOKEN).toString()

        setupAppBar()
        setScroll()
        getScan()
    }

    private fun setupAppBar() {
        binding.apply {
            topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun setScroll() {
        binding.apply {
            val threshold = (20 * resources.displayMetrics.density).toInt()
            nsvDisease.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                if (scrollY > threshold) {
                    topAppBar.navigationIcon = ContextCompat.getDrawable(this@ScanDiseaseActivity, R.drawable.ic_back_tint)
                    topAppBar.title = ""
                } else {
                    topAppBar.navigationIcon = ContextCompat.getDrawable(this@ScanDiseaseActivity, R.drawable.ic_back)
                    topAppBar.title = getString(R.string.scan_disease_title)
                }
            }
        }
    }

    private fun getScan() {
        binding.apply {
            scanViewModel.apply {
                isLoading.observe(this@ScanDiseaseActivity) {
                    val garden = scanDiseaseGarden.value
                    val field = scanDiseaseField.value
                    if (!it && garden?.isNotEmpty() == true && field?.isNotEmpty() == true) {
                        cpiDisease.visibility = View.GONE
                        showScan(garden, field)
                    } else {
                        cpiDisease.visibility = View.VISIBLE
                    }
                }

                getScan()
            }
        }
    }

    private fun showScan(garden: List<Scan>, field: List<Scan>) {
        val gardenAdapter = ScanAdapter(garden)
        val fieldAdapter = ScanAdapter(field)

        binding.apply {
            rvGarden.apply {
                adapter = gardenAdapter
                layoutManager = GridLayoutManager(this@ScanDiseaseActivity, 2)
            }
            gardenAdapter.setOnClickCallback(object: ScanAdapter.OnClickCallback {
                override fun onClicked(scan: Scan) {
                    Intent(this@ScanDiseaseActivity, ScanAddActivity::class.java).also {
                        it.putExtra(EXTRA_TOKEN, userToken)
                        it.putExtra(EXTRA_SCAN_ID, scan.id)
                        it.putExtra(EXTRA_SCAN_NAME, scan.name)
                        it.putExtra(EXTRA_SCAN_TYPE, "disease")
                        startActivity(it)
                    }
                }
            })

            rvField.apply {
                adapter = fieldAdapter
                layoutManager = GridLayoutManager(this@ScanDiseaseActivity, 2)
            }
            fieldAdapter.setOnClickCallback(object: ScanAdapter.OnClickCallback {
                override fun onClicked(scan: Scan) {
                    Intent(this@ScanDiseaseActivity, ScanAddActivity::class.java).also {
                        it.putExtra(EXTRA_TOKEN, userToken)
                        it.putExtra(EXTRA_SCAN_ID, scan.id)
                        it.putExtra(EXTRA_SCAN_NAME, scan.name)
                        it.putExtra(EXTRA_SCAN_TYPE, "disease")
                        startActivity(it)
                    }
                }
            })
        }
    }
}
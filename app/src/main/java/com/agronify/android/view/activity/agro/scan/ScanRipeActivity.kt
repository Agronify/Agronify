package com.agronify.android.view.activity.agro.scan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.agronify.android.databinding.ActivityScanRipeBinding
import com.agronify.android.model.remote.response.Scan
import com.agronify.android.util.Constants
import com.agronify.android.view.adapter.ScanAdapter
import com.agronify.android.viewmodel.ScanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanRipeActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityScanRipeBinding.inflate(layoutInflater)
    }
    private val scanViewModel: ScanViewModel by viewModels()
    private var userToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
    }

    private fun setupView() {
        userToken = intent.getStringExtra(Constants.EXTRA_TOKEN).toString()

        setupAppBar()
        getScan()
    }

    private fun setupAppBar() {
        binding.apply {
            topAppBar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun getScan() {
        binding.apply {
            scanViewModel.apply {
                isLoading.observe(this@ScanRipeActivity) {
                    val fruit = scanRipeness.value
                    if (!it && fruit?.isNotEmpty() == true) {
                        cpiRipe.visibility = View.GONE
                        showScan(fruit)
                    } else {
                        cpiRipe.visibility = View.VISIBLE
                    }
                }

                getScan()
            }
        }
    }

    private fun showScan(fruit: List<Scan>) {
        val fruitAdapter = ScanAdapter(fruit)

        binding.apply {
            rvRipe.apply {
                adapter = fruitAdapter
                layoutManager = GridLayoutManager(this@ScanRipeActivity, 2)
            }
            fruitAdapter.setOnClickCallback(object: ScanAdapter.OnClickCallback {
                override fun onClicked(scan: Scan) {
                    Intent(this@ScanRipeActivity, ScanAddActivity::class.java).also {
                        it.putExtra(Constants.EXTRA_TOKEN, userToken)
                        it.putExtra(Constants.EXTRA_SCAN_ID, scan.id)
                        it.putExtra(Constants.EXTRA_SCAN_NAME, scan.name)
                        it.putExtra(Constants.EXTRA_SCAN_TYPE, "ripeness")
                        startActivity(it)
                    }
                }
            })
        }
    }
}
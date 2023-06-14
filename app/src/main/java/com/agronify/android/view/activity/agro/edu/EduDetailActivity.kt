package com.agronify.android.view.activity.agro.edu

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.agronify.android.BuildConfig.BUCKET_URL
import com.agronify.android.R
import com.agronify.android.databinding.ActivityEduDetailBinding
import com.agronify.android.model.remote.response.Edu
import com.agronify.android.util.Constants.EXTRA_EDU
import com.agronify.android.view.adapter.EduTagAdapter
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EduDetailActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityEduDetailBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
    }

    private fun setupView() {
        hideStatusBar()
        setupAppBar()
        setScroll()
        showEdu()
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }

    private fun setupAppBar() {
        binding.topAppBar.apply {
            setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun setScroll() {
        binding.apply {
            val threshold = (256 * resources.displayMetrics.density).toInt()
            nsvEdu.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                if (scrollY > threshold) {
                    fabShareScroll.show()
                } else {
                    fabShareScroll.hide()
                }
            }
        }
    }

    private fun showEdu() {
        val edu = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_EDU, Edu::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_EDU)
        }

        binding.apply {
            intent.apply {
                val image = edu?.image
                Glide.with(this@EduDetailActivity)
                    .load(BUCKET_URL + image)
                    .into(ivEduImage)
                tvEduTitle.text = edu?.title
                tvEduDesc.text = edu?.content

                val tags = edu?.tags
                if (!tags.isNullOrEmpty()) {
                    rvEduTag.apply {
                        adapter = EduTagAdapter(tags)
                        layoutManager = LinearLayoutManager(this@EduDetailActivity, LinearLayoutManager.HORIZONTAL, false)
                    }
                }
            }

            fabShare.setOnClickListener {
                showSoonDialog()
            }

            fabShareScroll.setOnClickListener {
                showSoonDialog()
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
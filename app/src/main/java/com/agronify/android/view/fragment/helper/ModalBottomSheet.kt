package com.agronify.android.view.fragment.helper

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agronify.android.R
import com.agronify.android.databinding.DialogSheetBinding
import com.agronify.android.model.remote.response.PredictResponse
import com.agronify.android.util.Constants.EXTRA_SCAN_RESULT
import com.agronify.android.util.Constants.EXTRA_SCAN_SUCCESS
import com.agronify.android.util.Constants.EXTRA_SCAN_TYPE
import com.agronify.android.view.activity.agro.scan.ScanResultActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheet : BottomSheetDialogFragment() {
    private val binding by lazy {
        DialogSheetBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isSuccess = arguments?.getBoolean(EXTRA_SCAN_SUCCESS)
        val cropType = arguments?.getString(EXTRA_SCAN_TYPE)
        val imagePrediction = arguments?.getParcelable<PredictResponse>(EXTRA_SCAN_RESULT)

        binding.apply {
            if (isSuccess == true && imagePrediction?.result != "Not Detected") {
                when (cropType) {
                    "disease" -> {
                        if (imagePrediction?.result == "Healthy" || imagePrediction?.disease?.name == "Healthy") {
                            showResult("diseaseHealthySuccess")
                            tvDiseaseHealthyResult.text = if (imagePrediction?.result == "Healthy") imagePrediction.result else imagePrediction?.disease?.name
                            tvDiseaseHealthyAccuracy.text = getString(R.string.add_accuracy, imagePrediction.accuracy)
                            tvDiseaseHealthyBack.setOnClickListener {
                                dismiss()
                            }
                        } else {
                            showResult("diseaseSuccess")
                            tvDiseaseResult.text = if (imagePrediction?.disease != null) imagePrediction.disease.name else imagePrediction?.result
                            tvDiseaseAccuracy.text = getString(R.string.add_accuracy, imagePrediction?.accuracy)
                            btnDiseaseDetail.setOnClickListener {
                                Intent(requireActivity(), ScanResultActivity::class.java).also {
                                    it.putExtra(EXTRA_SCAN_RESULT, imagePrediction)
                                    startActivity(it)
                                    dismiss()
                                }
                            }
                            tvDiseaseBack.setOnClickListener {
                                dismiss()
                            }
                        }
                    }
                    "ripeness" -> {
                        showResult("ripenessSuccess")
                        tvRipenessResult.text = imagePrediction?.result
                        tvRipenessAccuracy.text = getString(R.string.add_accuracy, imagePrediction?.accuracy)
                        tvRipenessBack.setOnClickListener {
                            dismiss()
                        }
                    }
                }
            } else {
                showResult("failure")
                btnFailureRetake.setOnClickListener {
                    dismiss()
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        activity?.finish()
    }

    private fun showResult(result: String) {
        binding.apply {
            diseaseHealthySuccess.visibility = View.GONE
            diseaseSuccess.visibility = View.GONE
            ripenessSuccess.visibility = View.GONE
            failure.visibility = View.GONE

            when (result) {
                "diseaseHealthySuccess" -> {
                    diseaseHealthySuccess.visibility = View.VISIBLE
                }
                "diseaseSuccess" -> {
                    diseaseSuccess.visibility = View.VISIBLE
                }
                "ripenessSuccess" -> {
                    ripenessSuccess.visibility = View.VISIBLE
                }
                "failure" -> {
                    failure.visibility = View.VISIBLE
                }
            }
        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}
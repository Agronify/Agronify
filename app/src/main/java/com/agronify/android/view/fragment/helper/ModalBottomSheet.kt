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
            if (isSuccess == true && imagePrediction?.result != "NOT DETECTED") {
                when (cropType) {
                    "disease" -> {
                        diseaseSuccess.visibility = View.VISIBLE
                        ripenessSuccess.visibility = View.GONE
                        failure.visibility = View.GONE
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
                    "ripeness" -> {
                        diseaseSuccess.visibility = View.GONE
                        ripenessSuccess.visibility = View.VISIBLE
                        failure.visibility = View.GONE
                        tvRipenessResult.text = imagePrediction?.result
                        tvRipenessAccuracy.text = getString(R.string.add_accuracy, imagePrediction?.accuracy)
                        tvRipenessBack.setOnClickListener {
                            dismiss()
                        }
                    }
                }
            } else {
                diseaseSuccess.visibility = View.GONE
                ripenessSuccess.visibility = View.GONE
                failure.visibility = View.VISIBLE
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

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}
package com.agronify.android.viewmodel

import android.app.Activity.RESULT_OK
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agronify.android.model.remote.response.PredictResponse
import com.agronify.android.model.remote.response.Scan
import com.agronify.android.model.repository.ScanRepository
import com.agronify.android.util.CameraUtil.processFileCamera
import com.agronify.android.util.CameraUtil.processFileGallery
import com.agronify.android.util.CameraUtil.reduceFile
import com.agronify.android.util.CameraUtil.uriToFile
import com.agronify.android.util.Constants.CAMERA_X_ROTATION
import com.agronify.android.util.Constants.CAMERA_X_SUCCESS
import com.agronify.android.util.Constants.EXTRA_SCAN_IMAGE
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanRepository: ScanRepository
): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isUploaded = MutableLiveData<Boolean>()
    val isUploaded: LiveData<Boolean> = _isUploaded

    private val _prediction = MutableLiveData<PredictResponse>()
    val prediction: LiveData<PredictResponse> = _prediction

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _scanDiseaseGarden = MutableLiveData<List<Scan>>()
    val scanDiseaseGarden: LiveData<List<Scan>> = _scanDiseaseGarden

    private val _scanDiseaseField = MutableLiveData<List<Scan>>()
    val scanDiseaseField: LiveData<List<Scan>> = _scanDiseaseField

    private val _scanRipeness = MutableLiveData<List<Scan>>()
    val scanRipeness: LiveData<List<Scan>> = _scanRipeness

    private val _file = MutableLiveData<File>()
    val file: LiveData<File> = _file

    private val _previewBitmap = MutableLiveData<Bitmap>()
    val previewBitmap: LiveData<Bitmap> = _previewBitmap

    fun handleCameraActivityResult(result: ActivityResult) {
        if (result.resultCode == CAMERA_X_SUCCESS) {
            val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra(EXTRA_SCAN_IMAGE, File::class.java)
            } else {
                result.data?.getSerializableExtra(EXTRA_SCAN_IMAGE)
            } as? File
            val imageRotationDegrees = result.data?.getIntExtra(CAMERA_X_ROTATION, 0) as Int

            file?.let {
                processFileCamera(it, imageRotationDegrees)
                reduceFile(it)
                _file.value = it
                _previewBitmap.value = BitmapFactory.decodeFile(it.path)
            }
        }
    }

    fun handleGalleryActivityResult(result: ActivityResult, context: Context) {
        if (result.resultCode == RESULT_OK) {
            val selected = result.data?.data as Uri
            val file = uriToFile(selected, context)

            file.let {
                processFileGallery(it, 90)
                reduceFile(it)
                _file.value = it
                _previewBitmap.value = BitmapFactory.decodeFile(it.path)
            }
        }
    }

    fun getScan() = viewModelScope.launch {
        _isLoading.value = true

        runCatching {
            scanRepository.getScan()
        }.let { result ->
            result.onSuccess {
                setList(it)
                _isLoading.value = false
            }

            result.onFailure { e ->
                if (e is HttpException) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    val errorMessage = errorResponse?.let { JSONObject(it).getString("error") }
                    _error.value = errorMessage
                }
                _isLoading.value = false
            }
        }
    }

    private fun setList(scan: List<Scan>) {
        val ripeness = mutableListOf<Scan>()
        val disease = mutableListOf<Scan>()
        val diseaseGarden = mutableListOf<Scan>()
        val diseaseField = mutableListOf<Scan>()

        for (i in scan) {
            when (i.isFruit) {
                true -> ripeness.add(i)
                false -> disease.add(i)
            }
        }

        for (i in disease) {
            when (i.type) {
                "Perkebunan" -> diseaseGarden.add(i)
                "Pertanian" -> diseaseField.add(i)
            }
        }

        _scanDiseaseGarden.value = diseaseGarden
        _scanDiseaseField.value = diseaseField
        _scanRipeness.value = ripeness
    }

    fun uploadScan(token: String, file: File, id: Int, type: String) = viewModelScope.launch {
        _isLoading.value = true
        _isUploaded.value = false

        val image = file.asRequestBody("image/jpeg".toMediaType())
        val uploadType = "predicts".toRequestBody("text/plain".toMediaType())
        val multipart: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, image)

        runCatching {
            scanRepository.uploadScan(token, multipart, uploadType)
        }.let { result ->
            result.onSuccess {
                predictScan(token, id, type, it.path)
            }

            result.onFailure { e ->
                if (e is HttpException) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    val errorMessage = errorResponse?.let { JSONObject(it).getString("error") }
                    _error.value = errorMessage
                }
                _isLoading.value = false
            }
        }
    }

    private fun predictScan(token: String, id: Int, type: String, path: String) = viewModelScope.launch {
        val request = JsonObject().apply {
            addProperty("type", type)
            addProperty("crop_id", id)
            addProperty("path", path)
        }

        runCatching {
            scanRepository.predictScan(token, request)
        }.let { result ->
            result.onSuccess {
                _prediction.value = it
                _isUploaded.value = true
                _isLoading.value = false
            }

            result.onFailure { e ->
                if (e is HttpException) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    val errorMessage = errorResponse?.let { JSONObject(it).getString("error") }
                    _error.value = errorMessage
                }
                _isLoading.value = false
            }
        }
    }
}
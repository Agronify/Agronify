package com.agronify.android.viewmodel

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agronify.android.model.remote.response.Scan
import com.agronify.android.model.repository.ScanRepository
import com.agronify.android.util.CameraUtil.reduceFileImage
import com.agronify.android.util.CameraUtil.rescaleFile
import com.agronify.android.util.CameraUtil.uriToFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private val _cameraPermissionGranted = MutableLiveData<Boolean>()
    val cameraPermissionGranted: LiveData<Boolean> = _cameraPermissionGranted

    fun requestPermission(context: Context) {
        _cameraPermissionGranted.value = ContextCompat.checkSelfPermission(context, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, GALLERY_PERMISSION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, IMAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED
        if (_cameraPermissionGranted.value == false) {
            permissionLauncher.apply {
                launch(CAMERA_PERMISSION)
                launch(GALLERY_PERMISSION)
                launch(IMAGE_PERMISSION)
            }
        }
    }

    fun handleCameraActivityResult(result: ActivityResult) {
        if (result.resultCode == CAMERA_X_SUCCESS) {
            val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra("picture", File::class.java)
            } else {
                result.data?.getSerializableExtra("picture")
            } as? File
            val imageRotationDegree = result.data?.getIntExtra("rotation", 0) as Int

            file?.let {
                rescaleFile(it, imageRotationDegree)
                _file.value = it
                _previewBitmap.value = BitmapFactory.decodeFile(it.path)
            }
        }
    }

    fun handleGalleryActivityResult(result: ActivityResult, context: Context) {
        if (result.resultCode == RESULT_OK) {
            val selected = result.data?.data as Uri
            selected.let {
                val file = uriToFile(it, context)
                _file.value = file
                _previewBitmap.value = BitmapFactory.decodeFile(file.path)
            }
        }
    }

    fun uploadScan(file: File) = viewModelScope.launch {
        _isLoading.value = true
        _isUploaded.value = false

        val file = reduceFileImage(file)

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
                    val errorMessage = errorResponse?.let { JSONObject(it).getString("message") }
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
                "perkebunan" -> diseaseGarden.add(i)
                "pertanian" -> diseaseField.add(i)
            }
        }

        _scanDiseaseGarden.value = diseaseGarden
        _scanDiseaseField.value = diseaseField
        _scanRipeness.value = ripeness
    }

    private companion object {
        const val CAMERA_X_SUCCESS = 200
        const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        const val IMAGE_PERMISSION = Manifest.permission.READ_MEDIA_IMAGES
        const val GALLERY_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    }
}
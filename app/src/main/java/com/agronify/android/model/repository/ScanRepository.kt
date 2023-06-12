package com.agronify.android.model.repository

import com.agronify.android.model.remote.response.PredictResponse
import com.agronify.android.model.remote.response.Scan
import com.agronify.android.model.remote.response.UploadResponse
import com.agronify.android.model.remote.retrofit.ApiService
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ScanRepository @Inject constructor(
    private val apiService: ApiService,
    @Named("networkDispatcher") private val networkDispatcher: CoroutineDispatcher
) {
    suspend fun getScan(): List<Scan> {
        return withContext(networkDispatcher) {
            apiService.getScan().await()
        }
    }

    suspend fun uploadScan(token: String, file: MultipartBody.Part, type: RequestBody): UploadResponse {
        return withContext(networkDispatcher) {
            apiService.uploadScan("Bearer $token", file, type).await()
        }
    }

    suspend fun predictScan(token: String, request: JsonObject): PredictResponse {
        return withContext(networkDispatcher) {
            apiService.predictScan("Bearer $token", request).await()
        }
    }
}
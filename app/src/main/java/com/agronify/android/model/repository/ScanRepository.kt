package com.agronify.android.model.repository

import com.agronify.android.model.remote.response.Scan
import com.agronify.android.model.remote.retrofit.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
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
}
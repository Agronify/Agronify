package com.agronify.android.model.repository

import com.agronify.android.model.remote.response.Edu
import com.agronify.android.model.remote.retrofit.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class EduRepository @Inject constructor(
    private val apiService: ApiService,
    @Named("networkDispatcher") private val networkDispatcher: CoroutineDispatcher
) {
    suspend fun getEdu(): List<Edu> {
        return withContext(networkDispatcher) {
            apiService.getEdu().await()
        }
    }
}
package com.agronify.android.model.repository

import com.agronify.android.model.local.UserPreference
import com.agronify.android.model.remote.response.Auth
import com.agronify.android.model.remote.response.LoginResponse
import com.agronify.android.model.remote.retrofit.ApiService
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    @Named("diskDispatcher") private val diskDispatcher: CoroutineDispatcher,
    @Named("networkDispatcher") private val networkDispatcher: CoroutineDispatcher
){
    suspend fun userLogin(request: JsonObject): LoginResponse {
        return withContext(networkDispatcher) {
            apiService.userLogin(request).await()
        }
    }

    suspend fun userRegister(request: JsonObject) {
        return withContext(networkDispatcher) {
            apiService.userRegister(request).await()
        }
    }

    suspend fun getNew(): Boolean {
        return userPreference.getNew() ?: true
    }

    suspend fun saveNew(state: Boolean) {
        return withContext(diskDispatcher) {
            userPreference.saveNew(state)
        }
    }

    suspend fun getToken(): String {
        return userPreference.getToken() ?: ""
    }

    suspend fun saveToken(token: String) {
        return withContext(diskDispatcher) {
            userPreference.saveToken(token)
        }
    }

    suspend fun getUser(): Auth {
        return userPreference.getUser()
    }

    suspend fun saveUser(name: String, email: String) {
        return withContext(diskDispatcher) {
            userPreference.saveUser(name, email)
        }
    }

    suspend fun clearUser() {
        return withContext(diskDispatcher) {
            userPreference.clearUser()
        }
    }
}
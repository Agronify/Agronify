package com.agronify.android.model.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.agronify.android.model.remote.response.Auth
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserPreference @Inject constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun getNew(): Boolean? {
        return dataStore.data.first()[NEW_KEY]
    }

    suspend fun saveNew(state: Boolean) {
        dataStore.edit {
            it[NEW_KEY] = state
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data.first()[TOKEN_KEY]
    }

    suspend fun saveToken(token: String) {
        dataStore.edit {
            it[TOKEN_KEY] = token
        }
    }

    suspend fun getUser(): Auth {
        return dataStore.data.first().let {
            Auth(
                it[NAME_KEY] ?: "",
                it[EMAIL_KEY] ?: ""
            )
        }
    }

    suspend fun saveUser(name: String, email: String) {
        dataStore.edit {
            it[NAME_KEY] = name
            it[EMAIL_KEY] = email
        }
    }

    suspend fun clearUser() {
        dataStore.edit {
            it.remove(TOKEN_KEY)
            it.remove(NAME_KEY)
            it.remove(EMAIL_KEY)
        }
    }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NEW_KEY = booleanPreferencesKey("new")
    }
}
package com.app.qaimobile.data.datastore

import android.content.Context
import android.content.SharedPreferences
import com.app.qaimobile.domain.datastore.AppDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AppDataStoreImpl(private val context: Context) : AppDataStore {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_CREDENTIALS = "user_credentials"
    }

    override suspend fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
        log("Access token saved: $token")
    }

    override fun getAccessToken(): Flow<String?> {
        return flow {
            emit(sharedPreferences.getString(KEY_ACCESS_TOKEN, null))
        }.map {
            log("Access token retrieved: $it")
            it
        }
    }

    override suspend fun saveIsLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    override fun getIsLoggedIn(): Flow<Boolean> {
        return flow {
            emit(sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false))
        }
    }

    override suspend fun saveUserCredentials(email: String, password: String) {
        sharedPreferences.edit().putStringSet(KEY_USER_CREDENTIALS, setOf(email, password)).apply()
    }

    override fun getUserCredentials(): Flow<Pair<String?, String?>> {
        return flow {
            val credentials = sharedPreferences.getStringSet(KEY_USER_CREDENTIALS, null)
            val email = credentials?.firstOrNull()
            val password = credentials?.lastOrNull()
            emit(email to password)
        }
    }

    private fun log(message: String) {
        println(message) // or use a logging library like Timber
    }
}
